package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import org.agh.falsefriendapp.viewmodel.TranslationExerciseViewModel

@Composable
fun TranslationExerciseScreen(viewModel: TranslationExerciseViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        TranslationExerciseScreen()
    }
}
