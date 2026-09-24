package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.data.repository.ExerciseRepository
import javax.inject.Inject

private const val TAG = "DefinitionExerciseViewModel"

@HiltViewModel
class DefinitionExerciseViewModel @Inject constructor(
    repository: ExerciseRepository
) : BaseExerciseViewModel(repository) {
    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            try {
                val exercises = repository.getExercises(ExerciseType.DEFINITION)

                if (exercises.isEmpty()) {
                    setError("Empty list")
                }
                else {
                    setSuccess(exercises)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val msg = "Failed to fetch exercises"
                Log.e(TAG, msg, e)
                setError(msg)
            }
        }
    }
}
