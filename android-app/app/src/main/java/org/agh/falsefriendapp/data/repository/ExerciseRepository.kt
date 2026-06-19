package org.agh.falsefriendapp.data.repository

import org.agh.falsefriendapp.data.model.TranslationExercise

class ExerciseRepository {
    fun getTranslationExercises(size: Int): List<TranslationExercise> {
        return listOf(
            TranslationExercise(
                id = "0",
                sentence = "woda",
                options = listOf("house", "flower", "water", "school"),
                correctAnswerIndex = 1
            ),
            TranslationExercise(
                id = "1",
                sentence = "Stolica Małopolski to:",
                options = listOf("Tarnów", "Gdańsk", "Kraków", "Warszawa"),
                correctAnswerIndex = 2
            )
        )
    }

    fun getDefinitionExercises(size: Int): List<TranslationExercise> {
        return getTranslationExercises(size)
    }
}
