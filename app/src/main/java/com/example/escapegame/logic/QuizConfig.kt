package com.example.escapegame.logic

import androidx.annotation.StringRes

data class QuizConfig(
    @StringRes val questionRes: Int,
    @StringRes val optionARes: Int,
    @StringRes val optionBRes: Int,
    @StringRes val optionCRes: Int,
    val correctIndex: Int,
    @StringRes val explanationRes: Int,
)
