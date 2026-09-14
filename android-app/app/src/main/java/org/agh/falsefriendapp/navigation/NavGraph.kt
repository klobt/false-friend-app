package org.agh.falsefriendapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.ui.screens.DefinitionExerciseScreen
import org.agh.falsefriendapp.ui.screens.MatchExerciseScreen
import org.agh.falsefriendapp.ui.screens.ReviewScreen
import org.agh.falsefriendapp.ui.screens.SettingsScreen
import org.agh.falsefriendapp.ui.screens.SummaryScreen
import org.agh.falsefriendapp.ui.screens.TranslationExerciseScreen
import org.agh.falsefriendapp.ui.screens.UserMainScreen
import org.agh.falsefriendapp.viewmodel.SessionReviewViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val sessionReviewViewModel: SessionReviewViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.USER_HOME) {
        composable(Routes.USER_HOME) {
            UserMainScreen(
                onStartTranslation = {
                    navController.navigate(Routes.TRANSLATION) {
                        launchSingleTop = true
                    }
                },
                onStartDefinition = {
                    navController.navigate(Routes.DEFINITION) {
                        launchSingleTop = true
                    }
                },
                onStartMatch = {
                    navController.navigate(Routes.MATCH) {
                        launchSingleTop = true
                    }
                },
                onStartSettings = {
                    navController.navigate(Routes.SETTINGS) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.TRANSLATION) {
            TranslationExerciseScreen(
                onFinished = { score, totalQuestions, reviewItems ->
                    sessionReviewViewModel.setItems(reviewItems)
                    navController.navigate(Routes.summary(score, totalQuestions, ExerciseType.TRANSLATION)) {
                        popUpTo(Routes.TRANSLATION) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        composable(Routes.DEFINITION) {
            DefinitionExerciseScreen(
                onFinished = { score, totalQuestions, reviewItems ->
                    sessionReviewViewModel.setItems(reviewItems)
                    navController.navigate(Routes.summary(score, totalQuestions, ExerciseType.DEFINITION)) {
                        popUpTo(Routes.DEFINITION) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        composable(Routes.MATCH) {
            MatchExerciseScreen(
                onFinished = { score, totalQuestions, reviewItems ->
                    sessionReviewViewModel.setItems(reviewItems)
                    navController.navigate(Routes.summary(score, totalQuestions, ExerciseType.MATCH)) {
                        popUpTo(Routes.MATCH) { inclusive = true }
                        launchSingleTop = true
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
                onShowAnswers = {
                    navController.navigate(Routes.REVIEW) { launchSingleTop = true }
                },
                onNavigateHome = {
                    navController.popBackStack(route = Routes.USER_HOME, inclusive = false)
                }
            )
        }

        composable(Routes.REVIEW) {
            val items by sessionReviewViewModel.items.collectAsState()
            ReviewScreen(items = items, onNavigateBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateHome = { navController.popBackStack() }
            )
        }
    }
}
