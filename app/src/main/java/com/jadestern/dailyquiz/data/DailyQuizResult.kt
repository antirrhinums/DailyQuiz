package com.jadestern.dailyquiz.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "quiz_results")
data class DailyQuizResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: LocalDateTime,
    val score: Int,
    val category: String,
    val difficulty: String
)