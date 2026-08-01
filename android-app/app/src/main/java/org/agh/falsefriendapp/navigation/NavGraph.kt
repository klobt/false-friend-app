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
                onFinished = { score ->
                    navController.navigate("summary/$score") {
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
                onFinished = { score ->
                    navController.navigate("summary/$score") {
                        popUpTo("definition") { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(route = "userHome", inclusive = false)
                }
            )
        }

        composable("summary/{score}") { backStackEntry ->
            val scoreString = backStackEntry.arguments?.getString("score")
            val score = scoreString?.toIntOrNull() ?: 0
            SummaryScreen(
                score = score,
                onNavigateHome = {
                    navController.popBackStack(route = "userHome", inclusive = false)
                }
            )
        }
    }
}
