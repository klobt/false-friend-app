package org.agh.falsefriendapp.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.agh.falsefriendapp.ui.screens.TranslationExerciseScreen
import org.agh.falsefriendapp.ui.screens.UserMainScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "userHome") {
        composable("userHome") {
            UserMainScreen(onStartLearning = {navController.navigate("lesson")})
        }

        composable("lesson") {
            TranslationExerciseScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        NavGraph()
    }
}
