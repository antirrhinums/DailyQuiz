package com.jadestern.dailyquiz.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jadestern.dailyquiz.data.AppDatabase
import com.jadestern.dailyquiz.data.DailyQuizResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val MIGRATION_ANSWERS_ADDED = object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase){
            database.execSQL("""
                CREATE TABLE quiz_answers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    quizResultId INTEGER NOT NULL,
                    question TEXT NOT NULL,
                    correctAnswer TEXT NOT NULL,
                    userAnswer TEXT,
                    allAnswers TEXT NOT NULL,
                    FOREIGN KEY(quizResultId) REFERENCES quiz_results(id) ON DELETE CASCADE
                )
            """)
            database.execSQL("CREATE INDEX index_quiz_answers_quizResultId ON quiz_answers(quizResultId)")
        }
    }
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "daily_quiz_db"
        )
            .addMigrations(MIGRATION_ANSWERS_ADDED)
            .build()
    }

    @Provides
    @Singleton
    fun provideQuizResultDao(database: AppDatabase): DailyQuizResultDao {
        return database.quizResultDao()
    }
}