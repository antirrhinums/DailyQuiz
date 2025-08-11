package com.jadestern.dailyquiz.data

import android.text.Html
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.SerializedName

data class OpenTriviaResponse (
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("results") val results : List<OpenTriviaQuestion>
)

data class OpenTriviaQuestion(
    @SerializedName("category") val _category: String,
    @SerializedName("type") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("question") val _question: String,
    @SerializedName("correct_answer") val _correctAnswer: String,
    @SerializedName("incorrect_answers") val _incorrectAnswers: List<String>
){
    val question : String
        get() = HtmlCompat.fromHtml(_question, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()

    val correctAnswer: String
        get() = HtmlCompat.fromHtml(_correctAnswer, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()

    val incorrectAnswers: List<String>
        get() = _incorrectAnswers.map { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT).toString() }

    val category: String
        get() = HtmlCompat.fromHtml(_category, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
}
