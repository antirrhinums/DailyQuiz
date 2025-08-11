package com.jadestern.dailyquiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadestern.dailyquiz.data.DailyQuizAnswer
import com.jadestern.dailyquiz.data.DailyQuizResult
import com.jadestern.dailyquiz.data.DailyQuizResultDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val quizResultDao: DailyQuizResultDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(DetailsState())
        private set

    init {
        val quizResultId: Int = savedStateHandle.get<Int>("quizResultId") ?: -1
        viewModelScope.launch {
            val answers = quizResultDao.getAnswersForResult(quizResultId)
            val result = quizResultDao.getAllResults().first().find { it.id == quizResultId }

            state = state.copy(
                answers = answers,
                category = result?.category ?: "General Knowledge",
                difficulty = result?.difficulty ?: "easy"
            )
        }
    }
}

data class DetailsState(
    val answers: List<DailyQuizAnswer> = emptyList(),
    val category: String = "",
    val difficulty: String = ""
)