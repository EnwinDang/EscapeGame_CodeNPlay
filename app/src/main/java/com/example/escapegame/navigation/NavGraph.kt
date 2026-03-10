package com.example.escapegame.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.escapegame.R
import com.example.escapegame.logic.wordQuizMap
import com.example.escapegame.screens.BinaryGameScreen
import com.example.escapegame.screens.CongratulationsScreen
import com.example.escapegame.screens.DifficultyScreen
import com.example.escapegame.screens.ExternalGameScreen
import com.example.escapegame.screens.HomeScreen
import com.example.escapegame.screens.QuizScreen
import com.example.escapegame.screens.VideoScreen
import com.example.escapegame.viewmodel.Difficulty
import com.example.escapegame.viewmodel.GameViewModel

object Routes {
    const val HOME = "home"
    const val VIDEO = "video"
    const val DIFFICULTY = "difficulty"
    const val BINARY_GAME = "binary_game"
    const val BINARY_QUIZ = "binary_quiz/{word}"
    const val SCRATCH_GAME = "scratch_game"
    const val SCRATCH_QUIZ = "scratch_quiz"
    const val AI_GAME = "ai_game"
    const val AI_QUIZ = "ai_quiz"
    const val ROBOT_GAME = "robot_game"
    const val ROBOT_QUIZ = "robot_quiz"
    const val CONGRATULATIONS = "congratulations"
}

@Composable
fun NavGraph(navController: NavHostController, viewModel: GameViewModel) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onTap = { navController.navigate(Routes.VIDEO) }
            )
        }

        composable(Routes.VIDEO) {
            VideoScreen(
                onContinue = { navController.navigate(Routes.DIFFICULTY) }
            )
        }

        composable(Routes.DIFFICULTY) {
            DifficultyScreen(
                onDifficultySelected = { diff ->
                    viewModel.startGame(diff)
                    navController.navigate(Routes.BINARY_GAME)
                }
            )
        }

        composable(Routes.BINARY_GAME) {
            BinaryGameScreen(
                difficulty = viewModel.difficulty,
                onSolved = { word -> navController.navigate("binary_quiz/$word") }
            )
        }

        composable(
            route = Routes.BINARY_QUIZ,
            arguments = listOf(navArgument("word") { type = NavType.StringType })
        ) { backStackEntry ->
            val word = backStackEntry.arguments?.getString("word") ?: "DATA"
            val quiz = wordQuizMap[word] ?: wordQuizMap["DATA"]!!
            QuizScreen(
                question = stringResource(quiz.questionRes),
                options = listOf(
                    stringResource(quiz.optionARes),
                    stringResource(quiz.optionBRes),
                    stringResource(quiz.optionCRes)
                ),
                correctIndex = quiz.correctIndex,
                explanation = stringResource(quiz.explanationRes),
                onContinue = { navController.navigate(Routes.SCRATCH_GAME) }
            )
        }

        composable(Routes.SCRATCH_GAME) {
            ExternalGameScreen(
                stepNumber = 2,
                title = stringResource(R.string.title_scratch_game),
                instructions = stringResource(R.string.scratch_instructions),
                correctCode = "LOOP",
                onCodeCorrect = { navController.navigate(Routes.SCRATCH_QUIZ) }
            )
        }

        composable(Routes.SCRATCH_QUIZ) {
            QuizScreen(
                question = stringResource(R.string.scratch_quiz_question),
                options = listOf(
                    stringResource(R.string.scratch_quiz_option_a),
                    stringResource(R.string.scratch_quiz_option_b),
                    stringResource(R.string.scratch_quiz_option_c)
                ),
                correctIndex = 0,
                explanation = stringResource(R.string.scratch_quiz_explanation),
                onContinue = { navController.navigate(Routes.AI_GAME) }
            )
        }

        composable(Routes.AI_GAME) {
            ExternalGameScreen(
                stepNumber = 3,
                title = stringResource(R.string.title_ai_game),
                instructions = stringResource(R.string.ai_instructions),
                correctCode = "AI",
                onCodeCorrect = { navController.navigate(Routes.AI_QUIZ) }
            )
        }

        composable(Routes.AI_QUIZ) {
            QuizScreen(
                question = stringResource(R.string.ai_quiz_question),
                options = listOf(
                    stringResource(R.string.ai_quiz_option_a),
                    stringResource(R.string.ai_quiz_option_b),
                    stringResource(R.string.ai_quiz_option_c)
                ),
                correctIndex = 0,
                explanation = stringResource(R.string.ai_quiz_explanation),
                onContinue = { navController.navigate(Routes.ROBOT_GAME) }
            )
        }

        composable(Routes.ROBOT_GAME) {
            val robotInstructions = if (viewModel.difficulty == Difficulty.KIDS) {
                stringResource(R.string.robot_instructions_kids)
            } else {
                stringResource(R.string.robot_instructions_teens)
            }
            ExternalGameScreen(
                stepNumber = 4,
                title = stringResource(R.string.title_robot_game),
                instructions = robotInstructions,
                correctCode = "ROBOT",
                onCodeCorrect = { navController.navigate(Routes.ROBOT_QUIZ) }
            )
        }

        composable(Routes.ROBOT_QUIZ) {
            QuizScreen(
                question = stringResource(R.string.robot_quiz_question),
                options = listOf(
                    stringResource(R.string.robot_quiz_option_a),
                    stringResource(R.string.robot_quiz_option_b),
                    stringResource(R.string.robot_quiz_option_c)
                ),
                correctIndex = 0,
                explanation = stringResource(R.string.robot_quiz_explanation),
                onContinue = { navController.navigate(Routes.CONGRATULATIONS) }
            )
        }

        composable(Routes.CONGRATULATIONS) {
            CongratulationsScreen(
                totalTime = viewModel.getTotalTime(),
                difficulty = viewModel.difficulty,
                onPlayAgain = {
                    viewModel.reset()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
