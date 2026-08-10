package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.data.model.MatchExercise
import org.agh.falsefriendapp.ui.components.MatchExerciseButton
import org.agh.falsefriendapp.ui.state.MatchExerciseUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.MatchExerciseViewModel

@Composable
fun MatchExerciseScreen(
    viewModel: MatchExerciseViewModel = viewModel(),
    onNavigateHome: () -> Unit,
    onFinished: (score: Int, totalQuestions: Int) -> Unit,
    currentStep: Int,
    totalSteps: Int
) {
    val state by viewModel.state.collectAsState()
    MatchExerciseContent(
        state,
        onNavigateHome,
        onFinished,
        currentStep,
        totalSteps
    )
}

@Composable
fun MatchExerciseContent(
    state: MatchExerciseUiState,
    onNavigateHome: () -> Unit,
    onFinished: (score: Int, totalQuestions: Int) -> Unit,
    currentStep: Int,
    totalSteps: Int
) {
    when (state) {
        MatchExerciseUiState.Loading -> {
            LoadingScreen()
        }
        is MatchExerciseUiState.Error -> {
            ErrorScreen(state.message, onNavigateHome)
        }
        is MatchExerciseUiState.Success -> {
            MatchExerciseTask(
                state.exercises,
                currentStep,
                totalSteps
            )
        }
        is MatchExerciseUiState.Finished -> {
            LaunchedEffect(state) {
                onFinished(
                    state.correctAnswers,
                    state.totalQuestions
                )
            }
        }
    }
}

@Composable
fun MatchExerciseTask(
    exercises: List<MatchExercise>,
    currentStep: Int,
    totalSteps: Int
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseExerciseHeader(currentStep, totalSteps)
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Wybierz polskie słowo, a następnie jego angielski odpowiednik:",
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
        exercises.forEach { exercise ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    92.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                MatchExerciseButton(exercise.left)
                MatchExerciseButton(exercise.right)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchExerciseContentPreview() {
    FalseFriendAppTheme {
        MatchExerciseContent(
            state = MatchExerciseUiState.Success(
                listOf(
                    MatchExercise(0, "morze", "sea"),
                    MatchExercise(0, "dom", "house"),
                )
            ),
            onNavigateHome = {},
            onFinished = {_, _ -> },
            currentStep = 1,
            totalSteps = 5
        )
    }
}
