package com.jadestern.dailyquiz.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyQuizResultDao {
    @Insert
    suspend fun insertResult(quizResult: DailyQuizResult): Long

    @Insert
    suspend fun insertAnswers(answers: List<DailyQuizAnswer>)

    @Query("SELECT * FROM quiz_results ORDER BY dateTime DESC")
    fun getAllResults(): Flow<List<DailyQuizResult>>

    @Query("SELECT * FROM quiz_answers WHERE quizResultId = :quizResultId")
    suspend fun getAnswersForResult(quizResultId: Int): List<DailyQuizAnswer>

    @Query("DELETE FROM quiz_results WHERE id = :id")
    suspend fun deleteById(id: Int)
}