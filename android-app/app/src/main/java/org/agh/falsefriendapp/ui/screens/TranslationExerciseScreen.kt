package org.agh.falsefriendapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.ui.state.BaseExerciseUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.TranslationExerciseViewModel

@Composable
fun TranslationExerciseScreen(
    viewModel: TranslationExerciseViewModel = viewModel(),
    onNavigateHome: () -> Unit,
    onFinished: (score: Int, totalQuestions: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    TranslationExerciseContent(
        state,
        onNavigateHome,
        viewModel::onAnswerSelected,
        onFinished
    )
}

@Composable
fun TranslationExerciseContent(
    state: BaseExerciseUiState,
    onNavigateHome: () -> Unit,
    onAnswerSelected: (Int) -> Unit,
    onFinished: (score: Int, totalQuestions: Int) -> Unit
) {
    when (state) {
        BaseExerciseUiState.Loading -> {
            LoadingScreen()
        }
        is BaseExerciseUiState.Error -> {
            ErrorScreen(state.message, onNavigateHome)
        }
        is BaseExerciseUiState.Success -> {
            val currentExercise = state.exercises[state.currentIndex]

            BaseExerciseScreen(
                currentStep = state.currentIndex + 1,
                totalSteps = state.exercises.size,
                instruction = "Jak po angielsku powiemy:",
                exercise = currentExercise,
                onAnswerSelected = onAnswerSelected
            )
        }
        is BaseExerciseUiState.Finished -> {
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
fun TranslationExerciseContentPreview() {
    FalseFriendAppTheme {
        TranslationExerciseContent(
            state = BaseExerciseUiState.Success(
                listOf(BaseExercise(0, "lektura", 0, listOf(
                    "wykład", "lecture", "książka", "czytanie"
                ))),
                0,
                1
            ),
            onNavigateHome = {},
            onAnswerSelected = {},
            onFinished = {_, _ -> }
        )
    }
}
