package com.jadestern.dailyquiz.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jadestern.dailyquiz.data.converter.DateTimeConverter
import com.jadestern.dailyquiz.data.converter.ListStringConverter

@Database(entities = [DailyQuizResult::class, DailyQuizAnswer::class], version = 2, exportSchema = false)
@TypeConverters(DateTimeConverter::class, ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizResultDao(): DailyQuizResultDao
}