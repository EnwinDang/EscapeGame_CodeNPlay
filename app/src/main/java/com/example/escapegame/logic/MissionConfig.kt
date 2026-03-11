package com.example.escapegame.logic

import androidx.annotation.StringRes
import com.example.escapegame.R

enum class AgentUiStyle { JUNIOR, SENIOR }

data class MissionConfig(
    val binaryTimerSeconds: Int,
    val scratchCode: String,
    val aiCode: String,
    val robotCode: String,
    @StringRes val robotInstructionsRes: Int,
    val wordQuizMap: Map<String, QuizConfig>,
    val scratchQuiz: QuizConfig,
    val aiQuiz: QuizConfig,
    val robotQuiz: QuizConfig,
    val uiStyle: AgentUiStyle,
)

// =============================================
//  JUNIOR AGENT CONFIG  (Kids)
// =============================================
val JUNIOR_AGENT = MissionConfig(
    binaryTimerSeconds   = 15 * 60,
    scratchCode          = "LOOP",
    aiCode               = "AI",
    robotCode            = "ROBOT",
    robotInstructionsRes = R.string.robot_instructions_kids,

    wordQuizMap = juniorWordQuizMap,

    scratchQuiz = QuizConfig(
        questionRes    = R.string.scratch_quiz_question_kids,
        optionARes     = R.string.scratch_quiz_option_a_kids,
        optionBRes     = R.string.scratch_quiz_option_b_kids,
        optionCRes     = R.string.scratch_quiz_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.scratch_quiz_explanation_kids,
    ),
    aiQuiz = QuizConfig(
        questionRes    = R.string.ai_quiz_question_kids,
        optionARes     = R.string.ai_quiz_option_a_kids,
        optionBRes     = R.string.ai_quiz_option_b_kids,
        optionCRes     = R.string.ai_quiz_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.ai_quiz_explanation_kids,
    ),
    robotQuiz = QuizConfig(
        questionRes    = R.string.robot_quiz_question_kids,
        optionARes     = R.string.robot_quiz_option_a_kids,
        optionBRes     = R.string.robot_quiz_option_b_kids,
        optionCRes     = R.string.robot_quiz_option_c_kids,
        correctIndex   = 0,
        explanationRes = R.string.robot_quiz_explanation_kids,
    ),
    uiStyle = AgentUiStyle.JUNIOR,
)

// =============================================
//  SENIOR AGENT CONFIG  (Teens)
// =============================================
val SENIOR_AGENT = MissionConfig(
    binaryTimerSeconds   = 10 * 60,
    scratchCode          = "LOOP",
    aiCode               = "AI",
    robotCode            = "ROBOT",
    robotInstructionsRes = R.string.robot_instructions_teens,

    wordQuizMap = seniorWordQuizMap,

    scratchQuiz = QuizConfig(
        questionRes    = R.string.scratch_quiz_question_teens,
        optionARes     = R.string.scratch_quiz_option_a_teens,
        optionBRes     = R.string.scratch_quiz_option_b_teens,
        optionCRes     = R.string.scratch_quiz_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.scratch_quiz_explanation_teens,
    ),
    aiQuiz = QuizConfig(
        questionRes    = R.string.ai_quiz_question_teens,
        optionARes     = R.string.ai_quiz_option_a_teens,
        optionBRes     = R.string.ai_quiz_option_b_teens,
        optionCRes     = R.string.ai_quiz_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.ai_quiz_explanation_teens,
    ),
    robotQuiz = QuizConfig(
        questionRes    = R.string.robot_quiz_question_teens,
        optionARes     = R.string.robot_quiz_option_a_teens,
        optionBRes     = R.string.robot_quiz_option_b_teens,
        optionCRes     = R.string.robot_quiz_option_c_teens,
        correctIndex   = 0,
        explanationRes = R.string.robot_quiz_explanation_teens,
    ),
    uiStyle = AgentUiStyle.SENIOR,
)
