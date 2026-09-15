package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.ExerciseType

private const val TAG = "TranslationExerciseViewModel"

class TranslationExerciseViewModel : BaseExerciseViewModel() {
    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            try {
                val exercises = repository.getExercises(ExerciseType.TRANSLATION)

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
