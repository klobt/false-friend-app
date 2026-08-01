package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.ExerciseRepository

private const val TAG = "DefinitionExerciseViewModel"

class DefinitionExerciseViewModel : BaseExerciseViewModel() {
    private val repository = ExerciseRepository()

    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            try {
                val exercises = repository.getDefinitionExercises()

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
}
