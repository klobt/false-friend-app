package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.MatchExercise
import org.agh.falsefriendapp.data.repository.ExerciseRepository
import org.agh.falsefriendapp.ui.state.MatchConnection
import org.agh.falsefriendapp.ui.state.MatchExerciseSession
import org.agh.falsefriendapp.ui.state.MatchExerciseUiState
import org.agh.falsefriendapp.ui.state.MatchOption

private const val TAG = "MatchExerciseViewModel"

class MatchExerciseViewModel : ViewModel() {
    private val _state = MutableStateFlow<MatchExerciseUiState>(
        MatchExerciseUiState.Loading
    )
    val state = _state.asStateFlow()

    private val repository = ExerciseRepository()

    init {
        fetchExercises()
    }

    fun selectLeft(index: Int) {
        val currentState = _state.value
        if (currentState !is MatchExerciseUiState.Success) {
            return
        }

        val alreadyConnected = currentState.connections.any {
            it.leftIndex == index
        }
        if (alreadyConnected) {
            return
        }

        _state.value = currentState.copy(
            selectedLeft = index
        )
    }

    fun selectRight(originalIndex: Int) {
        val currentState = _state.value
        if (currentState !is MatchExerciseUiState.Success) {
            return
        }

        val selectedLeft = currentState.selectedLeft ?: return

        val alreadyConnected = currentState.connections.any {
            it.rightIndex == originalIndex
        }
        if (alreadyConnected) {
            return
        }

        val newConnection = MatchConnection(
            leftIndex = selectedLeft,
            rightIndex = originalIndex
        )
        val newConnections = currentState.connections + newConnection
        val currentExercise = currentState.exercises[currentState.currentIndex]
        val totalPairs = minOf(
            currentExercise.left.size,
            currentExercise.right.size
        )

        if (newConnections.size == totalPairs) {
            finishExercise(
                currentState = currentState,
                connections = newConnections
            )
        }
        else {
            _state.value = currentState.copy(
                selectedLeft = null,
                connections = newConnections
            )
        }
    }

    fun clearConnections() {
        val currentState = _state.value
        if (currentState !is MatchExerciseUiState.Success) {
            return
        }
        if (currentState.connections.isEmpty()) {
            return
        }

        _state.value = currentState.copy(
            selectedLeft = null,
            connections = currentState.connections.dropLast(1)
        )
    }

    private fun finishExercise(
        currentState: MatchExerciseUiState.Success,
        connections: List<MatchConnection>
    ) {
        val correctAnswers = connections.count {
            it.leftIndex == it.rightIndex
        }
        val totalCorrectAnswers = currentState.correctAnswers + correctAnswers
        val nextIndex = currentState.currentIndex + 1

        if (nextIndex < currentState.exercises.size) {
            _state.value = currentState.copy(
                currentIndex = nextIndex,
                selectedLeft = null,
                connections = emptyList(),
                correctAnswers = totalCorrectAnswers
            )
        }
        else {
            val totalQuestions = currentState.exercises.sumOf {
                minOf(it.left.size, it.right.size)
            }

            _state.value = MatchExerciseUiState.Finished(
                correctAnswers = totalCorrectAnswers,
                totalQuestions = totalQuestions
            )
        }
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            try {
                val exercises = repository.getMatchExercises()

                if (exercises.isEmpty()) {
                    setError("Empty list")
                }
                else {
                    setSuccess(exercises)
                }
            } catch (e: Exception) {
                // TODO dokladniejszy opis
                val msg = "Network error"
                Log.e(TAG, msg, e)
                setError(msg)
            }
        }
    }

    private fun setError(message: String) {
        _state.value = MatchExerciseUiState.Error(message)
    }

    private fun setSuccess(exercises: List<MatchExercise>) {
        val preparedExercises = exercises.map { exercise ->
            prepareExercise(exercise)
        }

        _state.value = MatchExerciseUiState.Success(
            exercises = preparedExercises,
            currentIndex = 0
        )
    }

    private fun prepareExercise(exercise: MatchExercise): MatchExerciseSession {
        val rightOptions = exercise.right.mapIndexed { index, text ->
            MatchOption(
                originalIndex = index,
                text = text
            )
        }.shuffled()
        return MatchExerciseSession(
            left = exercise.left,
            right = rightOptions
        )
    }
}
