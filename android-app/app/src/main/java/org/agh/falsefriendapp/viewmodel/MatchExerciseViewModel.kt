package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.MatchExercise
import org.agh.falsefriendapp.data.repository.ExerciseRepository
import org.agh.falsefriendapp.ui.state.MatchExerciseUiState

private const val TAG = "MatchExerciseViewModel"

class MatchExerciseViewModel : ViewModel() {
    private val _state = MutableStateFlow<MatchExerciseUiState>(MatchExerciseUiState.Loading)
    val state = _state.asStateFlow()

    private val repository = ExerciseRepository()

    init {
        fetchExercises()
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
        _state.value = MatchExerciseUiState.Success(
            exercises = exercises
        )
    }

    // TODO mechanika odpowiedzi
}
