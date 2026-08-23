package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.ui.components.MatchExerciseButton
import org.agh.falsefriendapp.ui.state.MatchConnection
import org.agh.falsefriendapp.ui.state.MatchExerciseSession
import org.agh.falsefriendapp.ui.state.MatchExerciseUiState
import org.agh.falsefriendapp.ui.state.MatchOption
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.MatchExerciseViewModel

@Composable
fun MatchExerciseScreen(
    viewModel: MatchExerciseViewModel = viewModel(),
    onFinished: (score: Int, totalQuestions: Int) -> Unit,
    onNavigateHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    MatchExerciseContent(
        state = state,
        onFinished = onFinished,
        onNavigateHome = onNavigateHome,
        onLeftSelected = viewModel::selectLeft,
        onRightSelected = viewModel::selectRight
    )
}

@Composable
fun MatchExerciseContent(
    state: MatchExerciseUiState,
    onFinished: (score: Int, totalQuestions: Int) -> Unit,
    onNavigateHome: () -> Unit,
    onLeftSelected: (Int) -> Unit,
    onRightSelected: (Int) -> Unit
) {
    when (state) {
        MatchExerciseUiState.Loading -> {
            LoadingScreen()
        }
        is MatchExerciseUiState.Error -> {
            ErrorScreen(state.message, onNavigateHome)
        }
        is MatchExerciseUiState.Success -> {
            val currentExercise = state.exercises[state.currentIndex]

            MatchExerciseTask(
                exercise = currentExercise,
                currentStep = state.currentIndex + 1,
                totalSteps = state.exercises.size,
                selectedLeft = state.selectedLeft,
                connections = state.connections,
                onLeftSelected = onLeftSelected,
                onRightSelected = onRightSelected
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
    exercise: MatchExerciseSession,
    currentStep: Int,
    totalSteps: Int,
    selectedLeft: Int?,
    connections: List<MatchConnection>,
    onLeftSelected: (Int) -> Unit,
    onRightSelected: (Int) -> Unit
) {
    val leftPositions = remember(exercise) {
        mutableStateMapOf<Int, Offset>()
    }
    val rightPositions = remember(exercise) {
        mutableStateMapOf<Int, Offset>()
    }
    var boardCoordinates by remember {
        mutableStateOf<LayoutCoordinates?>(null)
    }

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

        Box(
            modifier = Modifier.fillMaxWidth()
                .onGloballyPositioned {
                    boardCoordinates = it
                }
        ) {
            val lineColor = MaterialTheme.colorScheme.primary

            Canvas(modifier = Modifier.matchParentSize()) {
                connections.forEach { connection ->
                    val left = leftPositions[connection.leftIndex]
                    val right = rightPositions[connection.rightIndex]

                    if (left != null && right != null) {
                        drawLine(
                            color = lineColor,
                            start = left,
                            end = right,
                            strokeWidth = 5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                exercise.left.indices.forEach { index ->
                    val left = exercise.left[index]
                    val right = exercise.right[index]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            96.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        MatchExerciseButton(
                            text = left,
                            selected = selectedLeft == index,
                            matched = connections.any {
                                it.leftIndex == index
                            },
                            onClick = { onLeftSelected(index) },
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                boardCoordinates?.let { board ->
                                    val position = board.localPositionOf(coordinates)
                                    leftPositions[index] = Offset(
                                        x = position.x + coordinates.size.width / 2f,
                                        y = position.y + coordinates.size.height / 2f
                                    )
                                }
                            }
                        )
                        MatchExerciseButton(
                            text = right.text,
                            selected = false,
                            matched = connections.any {
                                it.rightIndex == right.originalIndex
                            },
                            onClick = { onRightSelected(right.originalIndex) },
                            modifier = Modifier.onGloballyPositioned { coordinates ->
                                boardCoordinates?.let { board ->
                                    val position = board.localPositionOf(coordinates)
                                    rightPositions[right.originalIndex] = Offset(
                                        x = position.x + coordinates.size.width / 2f,
                                        y = position.y + coordinates.size.height / 2f
                                    )
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
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
                    MatchExerciseSession(
                        listOf("morze", "dom", "samochód", "pies"),
                        listOf(
                            MatchOption(1, "house"),
                            MatchOption(3, "dog"),
                            MatchOption(2, "car"),
                            MatchOption(0, "sea"),
                        )
                    )
                ),
                currentIndex = 0
            ),
            onNavigateHome = {},
            onFinished = {_, _ -> },
            onLeftSelected = {},
            onRightSelected = {}
        )
    }
}
