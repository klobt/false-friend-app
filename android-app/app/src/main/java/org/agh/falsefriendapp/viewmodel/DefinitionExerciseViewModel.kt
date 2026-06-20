package org.agh.falsefriendapp.viewmodel

import org.agh.falsefriendapp.data.model.TranslationExercise
import org.agh.falsefriendapp.data.repository.ExerciseRepository

class DefinitionExerciseViewModel : BaseExerciseViewModel() {
//    private val repository = ExerciseRepository()
    override val exercises = listOf(
        TranslationExercise("1", "a", 1, listOf("a", "b"))
    )
}
