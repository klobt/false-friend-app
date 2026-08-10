package org.agh.falsefriendapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.ui.components.ExerciseButton
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme

@Composable
fun BaseExerciseScreen(
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    exercise: BaseExercise,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BaseExerciseHeader(currentStep, totalSteps)
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
        exercise.options.forEachIndexed { index, option ->
            ExerciseButton(text = option, onClick = { onAnswerSelected(index) })
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun BaseExerciseHeader(currentStep: Int, totalSteps: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(15.dp)
    ) {
        Text(
            "Pytanie $currentStep z $totalSteps",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(28.dp))

        val progress = (currentStep.toFloat() - 1) / totalSteps
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            label = "exercise_progress"
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            gapSize = (-8).dp,
            drawStopIndicator = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BaseExerciseScreenPreview() {
    FalseFriendAppTheme {
        BaseExerciseScreen(
            currentStep = 3,
            totalSteps = 10,
            instruction = "Jak po angielsku powiemy:",
            exercise = BaseExercise(
                0, "lektura", 1, listOf("lecture", "wykład", "czytanie", "lektor")
            ),
            onAnswerSelected = {}
        )
    }
}
