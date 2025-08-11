package com.jadestern.dailyquiz

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jadestern.dailyquiz.data.DailyQuizResult
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val results by viewModel.results.collectAsState()
    //val showDeleteToast by viewModel.showDeleteToast

    if (viewModel.showDeleteToast) {
        LaunchedEffect(Unit) {
            viewModel.clearToast()
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = { TextButton(onClick = { viewModel.clearToast() }) { Text("OK") } }
        ) {
            Text("Попытка удалена")
        }
    }

    if (results.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Вы еще не проходили ни одной викторины",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { result ->
                QuizResultItem(
                    result = result,
                    onDelete = { viewModel.deleteResult(result.id)},
                    onClick = {
                        Log.d("HistoryScreen", "Clicked on result with id: ${result.id}")
                        navController.navigate("details/${result.id}")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizResultItem(
    result: DailyQuizResult,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {onClick()},
                onLongClick = {showMenu = true}
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = result.dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Правильных ответов: ${result.score}/5",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Категория: ${result.category}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Сложность: ${result.difficulty}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Удалить") },
                    onClick = {
                        onDelete()
                        showMenu = false
                    }
                )
            }
        }
    }
}