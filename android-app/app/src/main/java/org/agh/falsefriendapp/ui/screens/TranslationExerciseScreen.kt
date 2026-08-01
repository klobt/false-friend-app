package org.agh.falsefriendapp.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.ui.state.ExerciseUiState
import org.agh.falsefriendapp.viewmodel.TranslationExerciseViewModel

@Composable
fun TranslationExerciseScreen(
    viewModel: TranslationExerciseViewModel = viewModel(),
    onFinished: (Int) -> Unit,
    onNavigateHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        ExerciseUiState.Loading -> {
            LoadingScreen()
        }
        is ExerciseUiState.Error -> {
            ErrorScreen(currentState.message, onNavigateHome)
        }
        is ExerciseUiState.Success -> {
            val currentExercise = currentState.exercises[currentState.currentIndex]

            BaseExerciseScreen(
                currentStep = currentState.currentIndex + 1,
                totalSteps = currentState.exercises.size,
                instruction = "Jak po angielsku powiemy:",
                exercise = currentExercise,
                onAnswerSelected = viewModel::onAnswerSelected
            )
        }
        is ExerciseUiState.Finished -> {
            LaunchedEffect(currentState) {
                onFinished(currentState.correctAnswers)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationScreenPreview() {
    MaterialTheme {
        TranslationExerciseScreen(onFinished = {}, onNavigateHome = {})
    }
}
