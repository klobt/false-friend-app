package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.ui.components.AppButton
import org.agh.falsefriendapp.viewmodel.DefinitionExerciseViewModel

@Composable
fun DefinitionExerciseScreen(
    viewModel: DefinitionExerciseViewModel = viewModel(),
    onFinished: (Int) -> Unit
) {
    val exercises = viewModel.exercises
    val exerciseIndex by viewModel.currentIndex.collectAsState()
    val currentExercise = exercises[exerciseIndex]
    val isFinished by viewModel.isFinished.collectAsState()

    LaunchedEffect(isFinished) {
        if (isFinished) {
            onFinished(viewModel.correctAnswers)
        }
    }

    if (isFinished) {
        return
    }

    BaseExerciseScreen(
        exerciseIndex + 1,
        exercises.size,
        "Wybierz definicję słowa:"
    ) {
        Box(
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentExercise.sentence,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        currentExercise.options.forEachIndexed { index, option ->
            AppButton(text = option, onClick = {viewModel.onAnswerSelected(index)})
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefinitionExerciseScreenPreview() {
    MaterialTheme {
        DefinitionExerciseScreen(onFinished = {})
    }
}
