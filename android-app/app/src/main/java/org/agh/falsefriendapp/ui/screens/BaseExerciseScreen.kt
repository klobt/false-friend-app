package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.ui.components.BaseExerciseHeader
import org.agh.falsefriendapp.ui.components.ExerciseButton
import org.agh.falsefriendapp.ui.components.FeedbackOverlay
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.ui.theme.exerciseColors

@Composable
fun BaseExerciseScreen(
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    exercise: BaseExercise,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit,
    accentColor: Color,
    containerColor: Color,
    onExit: () -> Unit
) {
    val feedback = selectedAnswerIndex?.let { it == exercise.correctAnswerIndex }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseExerciseHeader(
                currentStep,
                totalSteps,
                accentColor,
                containerColor,
                onExit
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = instruction,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .width(250.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = exercise.sentence,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            key(currentStep) {
                exercise.options.forEachIndexed { index, option ->
                    ExerciseButton(
                        text = option,
                        isCorrectOption = index == exercise.correctAnswerIndex,
                        isSelected = selectedAnswerIndex == index,
                        enabled = selectedAnswerIndex == null,
                        onClick = { onAnswerSelected(index) }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        FeedbackOverlay(feedback)
    }
}

@Preview(showBackground = true)
@Composable
private fun BaseExerciseScreenPreview() {
    FalseFriendAppTheme {
        BaseExerciseScreen(
            currentStep = 3,
            totalSteps = 10,
            instruction = "Jak po polsku powiemy:",
            exercise = BaseExercise(
                0, "lecture", 2, listOf("czytanie", "lektura", "wykład", "lektor")
            ),
            selectedAnswerIndex = null,
            onAnswerSelected = {},
            accentColor = MaterialTheme.exerciseColors.translationForeground,
            containerColor = MaterialTheme.exerciseColors.translationBackground,
            onExit = {}
        )
    }
}
