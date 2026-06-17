package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.ui.components.AppButton
import org.agh.falsefriendapp.viewmodel.TranslationExerciseViewModel

@Composable
fun TranslationExerciseScreen(
    viewModel: TranslationExerciseViewModel = viewModel(),
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

    if (!isFinished) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(currentExercise.sentence)
            Spacer(modifier = Modifier.height(40.dp))

            currentExercise.options.forEachIndexed { index, option ->
                AppButton(text = option, onClick = {viewModel.onAnswerSelected(index)})
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationScreenPreview() {
    MaterialTheme {
        TranslationExerciseScreen(onFinished = {})
    }
}
