package org.agh.falsefriendapp.ui.state

sealed class MatchExerciseUiState {
    data object Loading: MatchExerciseUiState()

    data class Success(
        val exercises: List<MatchExerciseSession>,
        val currentIndex: Int,
        val selectedLeft: Int? = null,
        val connections: List<MatchConnection> = emptyList(),
        val correctAnswers: Int = 0
    ): MatchExerciseUiState()

    data class Error(
        val message: String
    ): MatchExerciseUiState()

    data class Finished(
        val correctAnswers: Int,
        val totalQuestions: Int
    ): MatchExerciseUiState()
}
