package com.example.escapegame.logic

import com.example.escapegame.R

// ─────────────────────────────────────────────
//  JUNIOR  (Kids) — binary word quizzes
// ─────────────────────────────────────────────
val juniorWordQuizMap: Map<String, QuizConfig> = mapOf(
    "DATA" to QuizConfig(
        questionRes    = R.string.word_data_question_kids,
        optionARes     = R.string.word_data_option_a_kids,
        optionBRes     = R.string.word_data_option_b_kids,
        optionCRes     = R.string.word_data_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.word_data_explanation_kids,
    ),
    "CODE" to QuizConfig(
        questionRes    = R.string.word_code_question_kids,
        optionARes     = R.string.word_code_option_a_kids,
        optionBRes     = R.string.word_code_option_b_kids,
        optionCRes     = R.string.word_code_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.word_code_explanation_kids,
    ),
    "SERVER" to QuizConfig(
        questionRes    = R.string.word_server_question_kids,
        optionARes     = R.string.word_server_option_a_kids,
        optionBRes     = R.string.word_server_option_b_kids,
        optionCRes     = R.string.word_server_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.word_server_explanation_kids,
    ),
    "ROBOT" to QuizConfig(
        questionRes    = R.string.word_robot_question_kids,
        optionARes     = R.string.word_robot_option_a_kids,
        optionBRes     = R.string.word_robot_option_b_kids,
        optionCRes     = R.string.word_robot_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.word_robot_explanation_kids,
    ),
    "CLOUD" to QuizConfig(
        questionRes    = R.string.word_cloud_question_kids,
        optionARes     = R.string.word_cloud_option_a_kids,
        optionBRes     = R.string.word_cloud_option_b_kids,
        optionCRes     = R.string.word_cloud_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.word_cloud_explanation_kids,
    ),
)

// ─────────────────────────────────────────────
//  SENIOR  (Teens) — binary word quizzes
// ─────────────────────────────────────────────
val seniorWordQuizMap: Map<String, QuizConfig> = mapOf(
    "DATA" to QuizConfig(
        questionRes    = R.string.word_data_question_teens,
        optionARes     = R.string.word_data_option_a_teens,
        optionBRes     = R.string.word_data_option_b_teens,
        optionCRes     = R.string.word_data_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.word_data_explanation_teens,
    ),
    "CODE" to QuizConfig(
        questionRes    = R.string.word_code_question_teens,
        optionARes     = R.string.word_code_option_a_teens,
        optionBRes     = R.string.word_code_option_b_teens,
        optionCRes     = R.string.word_code_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.word_code_explanation_teens,
    ),
    "SERVER" to QuizConfig(
        questionRes    = R.string.word_server_question_teens,
        optionARes     = R.string.word_server_option_a_teens,
        optionBRes     = R.string.word_server_option_b_teens,
        optionCRes     = R.string.word_server_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.word_server_explanation_teens,
    ),
    "ROBOT" to QuizConfig(
        questionRes    = R.string.word_robot_question_teens,
        optionARes     = R.string.word_robot_option_a_teens,
        optionBRes     = R.string.word_robot_option_b_teens,
        optionCRes     = R.string.word_robot_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.word_robot_explanation_teens,
    ),
    "CLOUD" to QuizConfig(
        questionRes    = R.string.word_cloud_question_teens,
        optionARes     = R.string.word_cloud_option_a_teens,
        optionBRes     = R.string.word_cloud_option_b_teens,
        optionCRes     = R.string.word_cloud_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.word_cloud_explanation_teens,
    ),
)
