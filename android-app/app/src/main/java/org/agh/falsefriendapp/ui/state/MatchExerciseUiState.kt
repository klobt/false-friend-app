package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.MatchExercise

sealed class MatchExerciseUiState {
    data object Loading: MatchExerciseUiState()

    data class Success(
        val exercises: List<MatchExercise>
    ): MatchExerciseUiState()

    data class Error(
        val message: String
    ): MatchExerciseUiState()

    data class Finished(
        val correctAnswers: Int,
        val totalQuestions: Int
    ): MatchExerciseUiState()
}
