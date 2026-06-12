package org.agh.falsefriendapp.data.repository

import org.agh.falsefriendapp.data.model.TranslationExercise

class ExerciseRepository {
    fun getExercises(size: Int): List<TranslationExercise> {
        return listOf(
            TranslationExercise(
                id = "0",
                sentence = "Stolica Polski to:",
                options = listOf("Kraków", "Warszawa", "Gdańsk", "Poznań"),
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
}
