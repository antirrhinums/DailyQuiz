package com.jadestern.dailyquiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jadestern.dailyquiz.data.DailyQuizResult
import com.jadestern.dailyquiz.data.DailyQuizResultDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val quizResultDao: DailyQuizResultDao
) : ViewModel() {
    val results = quizResultDao.getAllResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var showDeleteToast by mutableStateOf(false)
        private set

    fun deleteResult(id: Int) {
        viewModelScope.launch {
            quizResultDao.deleteById(id)
            showDeleteToast = true
        }
    }

    fun clearToast() {
        showDeleteToast = false
    }
}