package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.TranslationExercise

sealed class ExerciseUiState {
    data object Loading: ExerciseUiState()

    data class Success(
        val exercises: List<TranslationExercise>,
        val currentIndex: Int,
        val correctAnswers: Int
    ): ExerciseUiState()

    data class Error(
        val message: String
    ): ExerciseUiState()

    data class Finished(
        val correctAnswers: Int,
        val totalQuestions: Int
    ): ExerciseUiState()
}
