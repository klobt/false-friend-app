package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.ui.state.BaseExerciseUiState

abstract class BaseExerciseViewModel : ViewModel() {
    private val _state = MutableStateFlow<BaseExerciseUiState>(BaseExerciseUiState.Loading)
    val state = _state.asStateFlow()

    protected fun setSuccess(exercises: List<BaseExercise>) {
        _state.value = BaseExerciseUiState.Success(
            exercises = exercises,
            currentIndex = 0,
            correctAnswers = 0
        )
    }

    protected fun setError(message: String) {
        _state.value = BaseExerciseUiState.Error(message)
    }

    fun onAnswerSelected(selectedIndex: Int) {
        val currentState = _state.value

        if (currentState !is BaseExerciseUiState.Success) {
            return
        }

        val currentExercise = currentState.exercises[currentState.currentIndex]
        val newCorrectAnswers = if (selectedIndex == currentExercise.correctAnswerIndex) {
            currentState.correctAnswers + 1
        } else {
            currentState.correctAnswers
        }

        nextQuestion(currentState, newCorrectAnswers)
    }

    private fun nextQuestion(currentState: BaseExerciseUiState.Success, newCorrectAnswers: Int) {
        val nextIndex = currentState.currentIndex + 1

        if (nextIndex < currentState.exercises.size) {
            _state.value = currentState.copy(
                currentIndex = nextIndex,
                correctAnswers = newCorrectAnswers
            )
        }
        else {
            _state.value = BaseExerciseUiState.Finished(
                correctAnswers = newCorrectAnswers,
                totalQuestions = currentState.exercises.size
            )
        }
    }
}
