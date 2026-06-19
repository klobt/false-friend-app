package org.agh.falsefriendapp.viewmodel

import org.agh.falsefriendapp.data.repository.ExerciseRepository

class TranslationExerciseViewModel : BaseExerciseViewModel() {
    private val repository = ExerciseRepository()
    override val exercises = repository.getTranslationExercises(DECK_SIZE)
}
