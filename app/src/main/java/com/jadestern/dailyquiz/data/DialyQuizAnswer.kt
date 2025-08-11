package com.jadestern.dailyquiz.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "quiz_answers",
    foreignKeys = [ForeignKey(
        entity = DailyQuizResult::class,
        parentColumns = ["id"],
        childColumns = ["quizResultId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["quizResultId"])]
)
data class DailyQuizAnswer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizResultId: Int,
    val question: String,
    val correctAnswer: String,
    val userAnswer: String?,
    val allAnswers: List<String>
)