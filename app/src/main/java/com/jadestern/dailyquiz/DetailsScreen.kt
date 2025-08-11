package com.jadestern.dailyquiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jadestern.dailyquiz.data.DailyQuizAnswer
import com.jadestern.dailyquiz.ui.theme.InactiveElement

@Composable
fun DetailsScreen(viewModel: DetailsViewModel = hiltViewModel()) {
    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Разбор викторины",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Категория: ${state.category}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Сложность: ${state.difficulty}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(state.answers) { index, answer ->
                AnswerItem(index + 1, answer, state.answers.size)
            }
        }
    }
}

@Composable
fun AnswerItem(questionNumber: Int, answer: DailyQuizAnswer, answerCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(

                ){
                    val iconRes = if (answer.userAnswer == answer.correctAnswer){
                        R.drawable.radio_correct
                    }
                    else{
                        R.drawable.radio_incorrect
                    }

                    Text(
                        text = "Вопрос $questionNumber из $answerCount",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InactiveElement
                    )

                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp),
                        tint = Color.Unspecified
                    )

                }
                Text(
                    text = answer.question,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                answer.allAnswers.forEach { option ->
                    val color = when {
                        option == answer.correctAnswer -> Color.Green
                        option == answer.userAnswer && option != answer.correctAnswer -> Color.Red
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    Text(
                        text = option,
                        color = color,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }
    }
}