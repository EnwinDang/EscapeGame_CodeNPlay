package com.example.escapegame.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.escapegame.R
import com.example.escapegame.screens.AiGameScreen
import com.example.escapegame.screens.BinaryGameScreen
import com.example.escapegame.screens.CongratulationsScreen
import com.example.escapegame.screens.DifficultyScreen
import com.example.escapegame.screens.ExternalGameScreen
import com.example.escapegame.screens.HomeScreen
import com.example.escapegame.screens.QuizScreen
import com.example.escapegame.screens.OutroScreen
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
    const val OUTRO = "outro"
    const val CONGRATULATIONS = "congratulations"
}

@Composable
fun NavGraph(navController: NavHostController, viewModel: GameViewModel) {
    val onHome: () -> Unit = {
        viewModel.reset()
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onTap = { navController.navigate(Routes.VIDEO) }
            )
        }

        composable(Routes.VIDEO) {
            VideoScreen(
                videoAssetManager = viewModel.videoAssetManager,
                onContinue = { navController.navigate(Routes.DIFFICULTY) }
            )
        }

        composable(Routes.DIFFICULTY) {
            DifficultyScreen(
                onDifficultySelected = { diff ->
                    viewModel.startGame(diff)
                    navController.navigate(Routes.BINARY_GAME)
                },
                onHome = onHome,
            )
        }

        composable(Routes.BINARY_GAME) {
            val config = viewModel.missionConfig
            BinaryGameScreen(
                videoAssetManager = viewModel.videoAssetManager,
                timerSeconds  = config.binaryTimerSeconds,
                onSolved      = { word -> navController.navigate("binary_quiz/$word") },
                onHome        = onHome,
            )
        }

        composable(
            route = Routes.BINARY_QUIZ,
            arguments = listOf(navArgument("word") { type = NavType.StringType })
        ) { backStackEntry ->
            val config = viewModel.missionConfig
            val word   = backStackEntry.arguments?.getString("word") ?: "DATA"
            val quiz   = config.wordQuizMap[word] ?: config.wordQuizMap["DATA"]!!
            QuizScreen(
                question     = stringResource(quiz.questionRes),
                options      = listOf(
                    stringResource(quiz.optionARes),
                    stringResource(quiz.optionBRes),
                    stringResource(quiz.optionCRes),
                ),
                correctIndex = quiz.correctIndex,
                explanation  = stringResource(quiz.explanationRes),
                uiStyle      = config.uiStyle,
                onContinue   = { navController.navigate(Routes.SCRATCH_GAME) },
                onHome       = onHome,
            )
        }

        composable(Routes.SCRATCH_GAME) {
            val config = viewModel.missionConfig
            ExternalGameScreen(
                stepNumber    = 2,
                title         = stringResource(R.string.title_scratch_game),
                instructions  = stringResource(R.string.scratch_instructions),
                correctCode   = config.scratchCode,
                onCodeCorrect = { navController.navigate(Routes.SCRATCH_QUIZ) },
                onHome        = onHome,
            )
        }

        composable(Routes.SCRATCH_QUIZ) {
            val config = viewModel.missionConfig
            QuizScreen(
                question     = stringResource(config.scratchQuiz.questionRes),
                options      = listOf(
                    stringResource(config.scratchQuiz.optionARes),
                    stringResource(config.scratchQuiz.optionBRes),
                    stringResource(config.scratchQuiz.optionCRes),
                ),
                correctIndex = config.scratchQuiz.correctIndex,
                explanation  = stringResource(config.scratchQuiz.explanationRes),
                uiStyle      = config.uiStyle,
                onContinue   = { navController.navigate(Routes.AI_GAME) },
                onHome       = onHome,
            )
        }

        composable(Routes.AI_GAME) {
            val config = viewModel.missionConfig
            if (viewModel.difficulty == Difficulty.KIDS) {
                AiGameScreen(
                    videoAssetManager = viewModel.videoAssetManager,
                    onGameCompleted = { navController.navigate(Routes.AI_QUIZ) },
                    onHome          = onHome,
                )
            } else {
                ExternalGameScreen(
                    stepNumber    = 3,
                    title         = stringResource(R.string.title_ai_game),
                    instructions  = stringResource(R.string.ai_instructions),
                    correctCode   = config.aiCode,
                    onCodeCorrect = { navController.navigate(Routes.AI_QUIZ) },
                    onHome        = onHome,
                )
            }
        }

        composable(Routes.AI_QUIZ) {
            val config = viewModel.missionConfig
            QuizScreen(
                question     = stringResource(config.aiQuiz.questionRes),
                options      = listOf(
                    stringResource(config.aiQuiz.optionARes),
                    stringResource(config.aiQuiz.optionBRes),
                    stringResource(config.aiQuiz.optionCRes),
                ),
                correctIndex = config.aiQuiz.correctIndex,
                explanation  = stringResource(config.aiQuiz.explanationRes),
                uiStyle      = config.uiStyle,
                onContinue   = { navController.navigate(Routes.ROBOT_GAME) },
                onHome       = onHome,
            )
        }

        composable(Routes.ROBOT_GAME) {
            val config = viewModel.missionConfig
            ExternalGameScreen(
                stepNumber    = 4,
                title         = stringResource(R.string.title_robot_game),
                instructions  = stringResource(config.robotInstructionsRes),
                correctCode   = config.robotCode,
                onCodeCorrect = { navController.navigate(Routes.ROBOT_QUIZ) },
                onHome        = onHome,
            )
        }

        composable(Routes.ROBOT_QUIZ) {
            val config = viewModel.missionConfig
            QuizScreen(
                question     = stringResource(config.robotQuiz.questionRes),
                options      = listOf(
                    stringResource(config.robotQuiz.optionARes),
                    stringResource(config.robotQuiz.optionBRes),
                    stringResource(config.robotQuiz.optionCRes),
                ),
                correctIndex = config.robotQuiz.correctIndex,
                explanation  = stringResource(config.robotQuiz.explanationRes),
                uiStyle      = config.uiStyle,
                onContinue   = { navController.navigate(Routes.OUTRO) },
                onHome       = onHome,
            )
        }

        composable(Routes.OUTRO) {
            OutroScreen(
                videoAssetManager = viewModel.videoAssetManager,
                onFinished = { navController.navigate(Routes.CONGRATULATIONS) }
            )
        }

        composable(Routes.CONGRATULATIONS) {
            CongratulationsScreen(
                totalTime   = viewModel.getTotalTime(),
                difficulty  = viewModel.difficulty,
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
