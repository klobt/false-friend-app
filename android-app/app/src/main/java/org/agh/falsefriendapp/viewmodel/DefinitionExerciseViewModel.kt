package org.agh.falsefriendapp.viewmodel

import org.agh.falsefriendapp.data.repository.ExerciseRepository

class DefinitionExerciseViewModel : BaseExerciseViewModel() {
    private val repository = ExerciseRepository()
    override val exercises = repository.getDefinitionExercises(DECK_SIZE)
}
