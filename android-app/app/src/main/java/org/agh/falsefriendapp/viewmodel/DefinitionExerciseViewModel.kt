package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.ExerciseRepository

class DefinitionExerciseViewModel : BaseExerciseViewModel() {
    private val repository = ExerciseRepository()

    init {
        fetchExercises()
    }

    private fun fetchExercises() {
        viewModelScope.launch {
            val result = repository.getDefinitionExercises()

            if (result.isNotEmpty()) {
                _exercises.value = result
            }
        }
    }
}
