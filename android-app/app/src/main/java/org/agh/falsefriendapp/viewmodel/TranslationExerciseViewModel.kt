package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.ExerciseRepository

private const val TAG = "TranslationExerciseViewModel"

class TranslationExerciseViewModel : BaseExerciseViewModel() {
    private val repository = ExerciseRepository()

    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            setLoading(true)
            try {
                _exercises.value = repository.getTranslationExercises()
            } catch (e: Exception) {
                val msg = "Network error"
                Log.e(TAG, msg, e)
            } finally {
                setLoading(false)
            }
        }
    }
}
