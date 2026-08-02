package org.agh.falsefriendapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.agh.falsefriendapp.ui.screens.DefinitionExerciseScreen
import org.agh.falsefriendapp.ui.screens.SummaryScreen
import org.agh.falsefriendapp.ui.screens.TranslationExerciseScreen
import org.agh.falsefriendapp.ui.screens.UserMainScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "userHome") {
        composable("userHome") {
            UserMainScreen(
                onStartTranslation = { navController.navigate("translation") },
                onStartDefinition = {navController.navigate("definition")}
            )
        }

        composable("translation") {
            TranslationExerciseScreen(
                onFinished = { score, totalQuestions ->
                    navController.navigate("summary/$score/$totalQuestions/translation") {
                        popUpTo("translation") { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = "userHome", inclusive = false)
                }
            )
        }

        composable("definition") {
            DefinitionExerciseScreen(
                onFinished = { score, totalQuestions ->
                    navController.navigate("summary/$score/$totalQuestions/definition") {
                        popUpTo("definition") { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = "userHome", inclusive = false)
                }
            )
        }

        composable("summary/{score}/{totalQuestions}/exerciseName") { backStackEntry ->
            val score = backStackEntry.arguments
                ?.getString("score")
                ?.toIntOrNull() ?: 0
            val totalQuestions = backStackEntry.arguments
                ?.getString("totalQuestions")
                ?.toIntOrNull() ?: 0
            val exerciseName = backStackEntry.arguments
                ?.getString("exerciseName") ?: ""
            SummaryScreen(
                score = score,
                totalQuestions = totalQuestions,
                exerciseName = exerciseName,
                onNavigateHome = {
                    navController.popBackStack(route = "userHome", inclusive = false)
                }
            )
        }
    }
}
