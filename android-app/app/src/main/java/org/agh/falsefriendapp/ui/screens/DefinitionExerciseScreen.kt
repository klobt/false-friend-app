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
import org.agh.falsefriendapp.viewmodel.DefinitionExerciseViewModel

@Composable
fun DefinitionExerciseScreen(
    viewModel: DefinitionExerciseViewModel = viewModel(),
    onFinished: (
        score: Int,
        totalQuestions: Int
    ) -> Unit,
    onNavigateHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    DefinitionExerciseContent(
        state,
        viewModel::onAnswerSelected,
        onFinished,
        onNavigateHome
    )
}

@Composable
fun DefinitionExerciseContent(
    state: ExerciseUiState,
    onAnswerSelected: (Int) -> Unit,
    onFinished: (score: Int, totalQuestions: Int) -> Unit,
    onNavigateHome: () -> Unit
) {
    when (state) {
        ExerciseUiState.Loading -> {
            LoadingScreen()
        }
        is ExerciseUiState.Error -> {
            ErrorScreen(state.message, onNavigateHome)
        }
        is ExerciseUiState.Success -> {
            val currentExercise = state.exercises[state.currentIndex]

            BaseExerciseScreen(
                currentStep = state.currentIndex + 1,
                totalSteps = state.exercises.size,
                instruction = "Jak po angielsku powiemy:",
                exercise = currentExercise,
                onAnswerSelected = onAnswerSelected
            )
        }
        is ExerciseUiState.Finished -> {
            LaunchedEffect(state) {
                onFinished(
                    state.correctAnswers,
                    state.totalQuestions
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefinitionExerciseContentPreview() {
    FalseFriendAppTheme {
        DefinitionExerciseContent(
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
