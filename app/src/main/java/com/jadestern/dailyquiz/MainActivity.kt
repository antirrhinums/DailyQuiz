package com.jadestern.dailyquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.jadestern.dailyquiz.ui.theme.ButtonBackground

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyQuizApp()
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuizApp() {
    val navController = rememberNavController()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ButtonBackground)
    ) {
        NavHost(
            navController = navController,
            startDestination = "quiz",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("quiz") { QuizScreen(navController) }
            composable("history") { HistoryScreen(navController) }
            composable(
                route = "details/{quizResultId}",
                arguments = listOf(navArgument("quizResultId") { type = NavType.IntType })
            ) { DetailsScreen() }
        }
    }

//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("DailyQuiz") },
//                actions = {
//                    IconButton(onClick = { navController.navigate("history") }) {
//                        Icon(Icons.Default.Menu, contentDescription = "History")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = "quiz",
////            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable("quiz") { QuizScreen() }
//            composable("history") { HistoryScreen(navController) }
//            composable(
//                route = "details/{quizResultId}",
//                arguments = listOf(navArgument("quizResultId") {type = NavType.StringType})
//            ) { DetailsScreen() }
//        }
//    }
}