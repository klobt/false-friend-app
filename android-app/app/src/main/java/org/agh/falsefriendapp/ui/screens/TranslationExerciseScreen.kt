package org.agh.falsefriendapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.data.model.TranslationExercise
import org.agh.falsefriendapp.ui.state.ExerciseUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.TranslationExerciseViewModel

@Composable
fun TranslationExerciseScreen(
    viewModel: TranslationExerciseViewModel = viewModel(),
    onFinished: (
        score: Int,
        totalQuestions: Int
    ) -> Unit,
    onNavigateHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    TranslationExerciseContent(
        state,
        viewModel::onAnswerSelected,
        onFinished,
        onNavigateHome
    )
}

@Composable
fun TranslationExerciseContent(
    state: ExerciseUiState,
    onAnswerSelected: (Int) -> Unit,
    onFinished: (score: Int, totalQuestions: Int) -> Unit,
    onNavigateHome: () -> Unit
) {
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
                onAnswerSelected = onAnswerSelected
            )
        }
        is ExerciseUiState.Finished -> {
            LaunchedEffect(currentState) {
                onFinished(
                    currentState.correctAnswers,
                    currentState.totalQuestions
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationExerciseContentPreview() {
    FalseFriendAppTheme {
        TranslationExerciseContent(
            state = ExerciseUiState.Success(
                listOf(TranslationExercise(0, "lektura", 0, listOf(
                    "wykład", "lecture", "książka", "czytanie"
                ))),
                0,
                1
            ),
            onAnswerSelected = {},
            onFinished = {_, _ -> },
            onNavigateHome = {}
        )
    }
}
