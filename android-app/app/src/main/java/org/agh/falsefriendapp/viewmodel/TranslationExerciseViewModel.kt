package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val TAG = "TranslationExerciseViewModel"

class TranslationExerciseViewModel : BaseExerciseViewModel() {
    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            try {
                val exercises = repository.getTranslationExercises()

                if (exercises.isEmpty()) {
                    setError("Empty list")
                }
                else {
                    setSuccess(exercises)
                }
            } catch (e: Exception) {
                val msg = "Failed to fetch exercises"
                Log.e(TAG, msg, e)
                setError(msg)
            }
        }
    }
}
