package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.BaseExercise

sealed class BaseExerciseUiState {
    data object Loading: BaseExerciseUiState()

    data class Success(
        val exercises: List<BaseExercise>,
        val currentIndex: Int
    ): BaseExerciseUiState()

    data class Error(
        val message: String
    ): BaseExerciseUiState()

    data class Finished(
        val correctAnswers: Int,
        val totalQuestions: Int
    ): BaseExerciseUiState()
}
