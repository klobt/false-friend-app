package org.agh.falsefriendapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.ui.screens.DefinitionExerciseScreen
import org.agh.falsefriendapp.ui.screens.MatchExerciseScreen
import org.agh.falsefriendapp.ui.screens.SettingsScreen
import org.agh.falsefriendapp.ui.screens.SummaryScreen
import org.agh.falsefriendapp.ui.screens.TranslationExerciseScreen
import org.agh.falsefriendapp.ui.screens.UserMainScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.USER_HOME) {
        composable(Routes.USER_HOME) {
            UserMainScreen(
                onStartTranslation = { navController.navigate(Routes.TRANSLATION) },
                onStartDefinition = { navController.navigate(Routes.DEFINITION) },
                onStartMatch = { navController.navigate(Routes.MATCH) },
                onStartSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.TRANSLATION) {
            TranslationExerciseScreen(
                onFinished = { score, totalQuestions ->
                    navController.navigate("summary/$score/$totalQuestions/translation") {
                        popUpTo(Routes.TRANSLATION) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        composable(Routes.DEFINITION) {
            DefinitionExerciseScreen(
                onFinished = { score, totalQuestions ->
                    navController.navigate("summary/$score/$totalQuestions/definition") {
                        popUpTo(Routes.DEFINITION) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        composable(Routes.MATCH) {
            MatchExerciseScreen(
                onFinished = { score, totalQuestions ->
                    navController.navigate("summary/$score/$totalQuestions/match") {
                        popUpTo(Routes.MATCH) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        composable(Routes.SUMMARY) { backStackEntry ->
            val score = backStackEntry.arguments
                ?.getString("score")
                ?.toIntOrNull() ?: 0
            val totalQuestions = backStackEntry.arguments
                ?.getString("totalQuestions")
                ?.toIntOrNull() ?: 0
            val exerciseType = backStackEntry.arguments
                ?.getString("exerciseType")
                ?.let { ExerciseType.fromValue(it) }
            SummaryScreen(
                score = score,
                totalQuestions = totalQuestions,
                exerciseType = exerciseType,
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateHome = { navController.popBackStack() }
            )
        }
    }
}
