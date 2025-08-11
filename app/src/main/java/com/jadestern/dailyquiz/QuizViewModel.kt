package com.jadestern.dailyquiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jadestern.dailyquiz.data.DailyQuizAnswer
import com.jadestern.dailyquiz.data.DailyQuizResult
import com.jadestern.dailyquiz.data.DailyQuizResultDao
import com.jadestern.dailyquiz.data.OpenTriviaApi
import com.jadestern.dailyquiz.data.OpenTriviaQuestion
import com.jadestern.dailyquiz.data.OpenTriviaCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDateTime

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val triviaApi: OpenTriviaApi,
    private val dailyQuizResultDao: DailyQuizResultDao
) : ViewModel() {
    var state by mutableStateOf(QuizState())
        private set

    private var timerJob: Job? = null
    private var feedbackJob: Job? = null
    private var timerTime = 300
    private val _remainingTime = MutableStateFlow(timerTime)
    val remainingTime = _remainingTime.asStateFlow()
    fun startQuiz() {
        state = state.copy(isLoading = true, error = null, isTimeUp = false)
        viewModelScope.launch {
            try {
                val response = triviaApi.getQuestions(
                    category = state.selectedCategory.id,
                    difficulty = state.selectedDifficulty?.lowercase()
                )
                val currentQuestion = response.results[0]
                val allAnswers = (currentQuestion.incorrectAnswers + currentQuestion.correctAnswer).shuffled()

                state = state.copy(
                    isSettingFilters = false,
                    questions = response.results,
                    currentQuestionIndex = 0,
                    currentQuestionAnswers = allAnswers,
                    isLoading = false,
                    userAnswers = List(response.results.size){null},
                    isQuizFinished = false
                )
                startTimer()
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = "Failed to load questions")
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _remainingTime.value = timerTime
        timerJob = viewModelScope.launch {
            while (_remainingTime.value > 0) {
                delay(1000)
                _remainingTime.value -= 1
                state.copy(
                    remainingTime = _remainingTime.value
                )
            }
            endQuizDueToTimeUp()
        }
    }

    private fun endQuizDueToTimeUp() {
        viewModelScope.launch {
            val quizResultId = dailyQuizResultDao.insertResult(
                DailyQuizResult(
                    dateTime = LocalDateTime.now(),
                    score = state.score,
                    category = state.questions.firstOrNull()?.category ?: "General Knowledge",
                    difficulty = state.questions.firstOrNull()?.difficulty ?: "easy"
                )
            ).toInt()
            val answers = state.questions.mapIndexed { index, question ->
                if(state.userAnswers[index] == null){
                    state.userAnswers.toMutableList().apply {
                        this[index] = question.incorrectAnswers.firstOrNull()
                    }
                }

                DailyQuizAnswer(
                    quizResultId = quizResultId,
                    question = question.question,
                    correctAnswer = question.correctAnswer,
                    userAnswer = state.userAnswers[index],
                    allAnswers = (question.incorrectAnswers + question.correctAnswer).shuffled()
                )
            }
            dailyQuizResultDao.insertAnswers(answers)
            state = state.copy(isTimeUp = true, isQuizFinished = true, showAnswerFeedback = false)
        }
    }

    fun selectAnswer(answer: String) {
        if(state.showAnswerFeedback) return

        val currentQuestion = state.questions[state.currentQuestionIndex]
        val isCorrect = answer == currentQuestion.correctAnswer
        val updatedAnswers = state.userAnswers.toMutableList().apply {
            this[state.currentQuestionIndex] = answer
        }
        state = state.copy(
            selectedAnswer = answer,
            score = if (isCorrect) state.score + 1 else state.score,
            userAnswers = updatedAnswers,
            isCorrectAnswer = isCorrect
        )
    }

    fun nextQuestion() {
        if(state.showAnswerFeedback) return

        state = state.copy(showAnswerFeedback = true)
        feedbackJob = viewModelScope.launch {
            delay(2000)
            state = state.copy(showAnswerFeedback = false)

            val nextIndex = state.currentQuestionIndex + 1
            if (nextIndex < state.questions.size) {
                val currentQuestion = state.questions[nextIndex]
                val allAnswers =
                    (currentQuestion.incorrectAnswers + currentQuestion.correctAnswer).shuffled()

                state = state.copy(
                    currentQuestionAnswers = allAnswers,
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null
                )
            } else {
                timerJob?.cancel()
                viewModelScope.launch {
                    val quizResultId = dailyQuizResultDao.insertResult(
                        DailyQuizResult(
                            dateTime = LocalDateTime.now(),
                            score = state.score,
                            category = state.questions.firstOrNull()?.category
                                ?: "General Knowledge",
                            difficulty = state.questions.firstOrNull()?.difficulty ?: "easy"
                        )
                    ).toInt()
                    val answers = state.questions.mapIndexed { index, question ->
                        DailyQuizAnswer(
                            quizResultId = quizResultId,
                            question = question.question,
                            correctAnswer = question.correctAnswer,
                            userAnswer = state.userAnswers[index],
                            allAnswers = (question.incorrectAnswers + question.correctAnswer).shuffled()
                        )
                    }
                    dailyQuizResultDao.insertAnswers(answers)
                    state = state.copy(isQuizFinished = true)
                }
            }
        }
    }

    fun resetQuiz() {
        timerJob?.cancel()
        _remainingTime.value = timerTime
        state = QuizState()
    }

    fun showFilters(show: Boolean){
        state = state.copy(
            isSettingFilters = show
        )
    }

    fun setCategory(category: OpenTriviaCategory){
        state = state.copy(selectedCategory = category)
    }

    fun setDifficulty(difficulty: String?){
        var diff: String? = "easy"

        when{
            difficulty == "Низкая" -> diff = "easy"
            difficulty == "Средняя" -> diff = "normal"
            difficulty == "Высокая" -> diff = "hard"
        }
        state = state.copy(selectedDifficulty = diff)
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

data class QuizState(
    val questions: List<OpenTriviaQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val currentQuestionAnswers: List<String> = emptyList(),
    val selectedAnswer: String? = null,
    val score: Int = 0,
    val userAnswers: List<String?> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isQuizFinished: Boolean = false,
    val isTimeUp: Boolean = false,
    val remainingTime: Int = 300,
    val selectedCategory: OpenTriviaCategory = OpenTriviaCategory.ANY,
    val selectedDifficulty: String? = null,
    val isSettingFilters: Boolean = false,
    val showAnswerFeedback: Boolean = false,
    val isCorrectAnswer: Boolean = false,
    val toNextQuestion: Boolean = true,
)