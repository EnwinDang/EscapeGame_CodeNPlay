package com.example.escapegame.logic

import com.example.escapegame.R

data class WordQuiz(
    val questionRes: Int,
    val optionARes: Int,
    val optionBRes: Int,
    val optionCRes: Int,
    val correctIndex: Int,
    val explanationRes: Int
)

val wordQuizMap: Map<String, WordQuiz> = mapOf(
    "DATA" to WordQuiz(
        questionRes    = R.string.word_data_question,
        optionARes     = R.string.word_data_option_a,
        optionBRes     = R.string.word_data_option_b,
        optionCRes     = R.string.word_data_option_c,
        correctIndex   = 0,
        explanationRes = R.string.word_data_explanation
    ),
    "CODE" to WordQuiz(
        questionRes    = R.string.word_code_question,
        optionARes     = R.string.word_code_option_a,
        optionBRes     = R.string.word_code_option_b,
        optionCRes     = R.string.word_code_option_c,
        correctIndex   = 0,
        explanationRes = R.string.word_code_explanation
    ),
    "SERVER" to WordQuiz(
        questionRes    = R.string.word_server_question,
        optionARes     = R.string.word_server_option_a,
        optionBRes     = R.string.word_server_option_b,
        optionCRes     = R.string.word_server_option_c,
        correctIndex   = 0,
        explanationRes = R.string.word_server_explanation
    ),
    "ROBOT" to WordQuiz(
        questionRes    = R.string.word_robot_question,
        optionARes     = R.string.word_robot_option_a,
        optionBRes     = R.string.word_robot_option_b,
        optionCRes     = R.string.word_robot_option_c,
        correctIndex   = 0,
        explanationRes = R.string.word_robot_explanation
    ),
    "CLOUD" to WordQuiz(
        questionRes    = R.string.word_cloud_question,
        optionARes     = R.string.word_cloud_option_a,
        optionBRes     = R.string.word_cloud_option_b,
        optionCRes     = R.string.word_cloud_option_c,
        correctIndex   = 0,
        explanationRes = R.string.word_cloud_explanation
    )
)
