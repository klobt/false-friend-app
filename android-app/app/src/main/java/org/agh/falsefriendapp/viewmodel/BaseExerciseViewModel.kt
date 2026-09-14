package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.data.model.Session
import org.agh.falsefriendapp.data.model.SessionResult
import org.agh.falsefriendapp.data.repository.ExerciseRepository
import org.agh.falsefriendapp.ui.state.BaseExerciseUiState

private const val TAG = "BaseExerciseViewModel"

abstract class BaseExerciseViewModel : ViewModel() {
    private val _state = MutableStateFlow<BaseExerciseUiState>(BaseExerciseUiState.Loading)
    val state = _state.asStateFlow()

    protected val repository = ExerciseRepository()
    private val sessionResults = mutableListOf<SessionResult>()
    private var totalCorrectAnswers = 0

    protected fun setSuccess(exercises: List<BaseExercise>) {
        sessionResults.clear()
        totalCorrectAnswers = 0

        _state.value = BaseExerciseUiState.Success(
            exercises = exercises,
            currentIndex = 0
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
        val correct = selectedIndex == currentExercise.correctAnswerIndex

        if (correct) {
            totalCorrectAnswers++
        }

        sessionResults += SessionResult(
            exerciseId = currentExercise.id,
            correct = correct,
            timeMs = 0L // TODO
        )

        nextQuestion(currentState)
    }

    private fun nextQuestion(currentState: BaseExerciseUiState.Success) {
        val nextIndex = currentState.currentIndex + 1

        if (nextIndex < currentState.exercises.size) {
            _state.value = currentState.copy(
                currentIndex = nextIndex
            )
        }
        else {
            finishSession(currentState)
        }
    }

    private fun finishSession(
        currentState: BaseExerciseUiState.Success
    ) {
        val session = Session(
            userId = 1, // TODO users
            results = sessionResults.toList()
        )

        viewModelScope.launch {
            try {
                repository.postSession(session)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to post session", e)
            }
        }

        _state.value = BaseExerciseUiState.Finished(
            correctAnswers = totalCorrectAnswers,
            totalQuestions = currentState.exercises.size
        )
    }
}
