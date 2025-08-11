package com.jadestern.dailyquiz

import android.graphics.Paint
import android.view.RoundedCorner
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jadestern.dailyquiz.data.OpenTriviaCategory
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.jadestern.dailyquiz.ui.theme.*

@Composable
fun QuizScreen(navController: NavController, viewModel: QuizViewModel = hiltViewModel()) {

    val state = viewModel.state
    val remainingTime by viewModel.remainingTime.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isTimeUp) {
        if(state.isTimeUp){
            Toast.makeText(context, "Время вышло! Викторина завершена.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.error != null -> {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.startQuiz() }) {
                    Text("Начать викторину")
                }
            }
            state.isQuizFinished || state.isTimeUp -> {
                Card (
                    shape = RoundedCornerShape(48.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(48.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ){
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Row(){
                            repeat(state.questions.size){ index->
                                val iconRes = if(index < state.score){
                                    R.drawable.star_active
                                }
                                else {
                                    R.drawable.star_inactive
                                }

                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .padding(4.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        }

                        val starCount = "${state.score} из ${state.questions.size}"

                        Text(
                            text = starCount,
                            color = StarColor,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        val result = when (state.score) {
                            5 -> "Идеально!" to "5/5 — вы ответили на всё правильно. Это блестящий результат!"
                            4 -> "Почти идеально!" to "4/5 — очень близко к совершенству. Ещё один шаг!"
                            3 -> "Хороший результат!" to "3/5 — вы на верном пути. Продолжайте тренироваться!"
                            2 -> "Есть над чем поработать" to "2/5 — не расстраивайтесь, попробуйте ещё раз!"
                            1 -> "Сложный вопрос?" to "1/5 — иногда просто не ваш день. Следующая попытка будет лучше!"
                            else -> "Бывает и так!" to "0/5 — не отчаивайтесь. Начните заново и удивите себя!"
                        }
                        Text(
                            text = result.first,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = result.second,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.resetQuiz() }) {
                            Text("Начать заново")
                        }
                    }
                }
            }

            state.questions.isNotEmpty() -> {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Логотип приложения",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Card (
                    shape = RoundedCornerShape(48.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(48.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Осталось времени: ${remainingTime / 60}:${
                                (remainingTime % 60).toString().padStart(2, '0')
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (remainingTime <= 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val question = state.questions[state.currentQuestionIndex]
                        Text(
                            text = "Вопрос ${state.currentQuestionIndex + 1} из 5",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = question.question,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        state.currentQuestionAnswers.forEach { answer ->
                            val borderColor = if (state.showAnswerFeedback) {
                                when {
                                    answer == state.questions[state.currentQuestionIndex].correctAnswer -> AnswerCorrect
                                    answer == state.selectedAnswer && !state.isCorrectAnswer -> AnswerIncorrect
                                    else -> Color.Transparent
                                }
                            } else {
                                Color.Transparent
                            }

                            val iconRes = if (state.showAnswerFeedback) {
                                when {
                                    answer == state.questions[state.currentQuestionIndex].correctAnswer -> R.drawable.radio_correct
                                    answer == state.selectedAnswer && !state.isCorrectAnswer -> R.drawable.radio_incorrect
                                    else -> R.drawable.radio_default
                                }
                            } else {
                                when {
                                    answer == state.selectedAnswer -> R.drawable.radio_selected
                                    else -> R.drawable.radio_default
                                }
                            }

                            Card(
                                shape = RoundedCornerShape(48.dp),
                                modifier = Modifier
                                    //.fillMaxWidth()
                                    .padding(16.dp)
                                    .border(
                                        2.dp,
                                        borderColor,
                                        RoundedCornerShape(48.dp)
                                    )
                                    .clip(RoundedCornerShape(48.dp))
                                    .clickable { viewModel.selectAnswer(answer) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .padding(4.dp),
                                        tint = Color.Unspecified
                                    )

                                    Text(
                                        text = answer,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(1f)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Вернуться к предыдущим вопросам нельзя",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            enabled = state.selectedAnswer != null
                        ) {
                            Text("Далее")
                        }
                    }
                }
            }
            state.isSettingFilters ->{
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Логотип приложения",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Card (
                    shape = RoundedCornerShape(48.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(48.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ){
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "Почти готовы!",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Осталось выбрать категорию и сложность викторины.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )

                        FilterDropdown(
                            label = "Категория",
                            selected = state.selectedCategory.displayName,
                            options = OpenTriviaCategory.entries.map { it.displayName },
                            onSelect = { displayName ->
                                val category = OpenTriviaCategory.entries.find { it.displayName == displayName } ?: OpenTriviaCategory.ANY
                                viewModel.setCategory(category)
                            }
                        )

                        val diff = when{
                            state.selectedDifficulty == "easy" -> "Низкая"
                            state.selectedDifficulty == "normal" -> "Средняя"
                            state.selectedDifficulty == "hard" -> "Высокая"
                            else -> "Низкая"
                        }

                        FilterDropdown(

                            label = "Сложность",
                            selected = diff,
                            options = listOf("Низкая", "Средняя", "Высокая"),
                            onSelect = { difficulty ->
                                viewModel.setDifficulty(difficulty)
                            }
                        )

                        Button(onClick = { viewModel.startQuiz() }) {
                            Text("Далее")
                        }
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                ){
                    Button(
                        onClick = {navController.navigate("history")},
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    ) {
                        Text("История")
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Логотип приложения",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Card (
                    shape = RoundedCornerShape(48.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(48.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ){
                    Spacer(modifier = Modifier.height(26.dp))
                    Text(
                        text = "Добро пожаловать в DailyQuiz!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    Button(
                        onClick = { viewModel.showFilters(true) },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Начать викторину")
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selected)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}