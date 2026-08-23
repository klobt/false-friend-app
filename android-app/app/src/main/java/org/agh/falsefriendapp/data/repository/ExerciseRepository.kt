package org.agh.falsefriendapp.data.repository

import org.agh.falsefriendapp.data.api.RetrofitClient
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.data.model.MatchExercise

class ExerciseRepository {
    suspend fun getTranslationExercises(): List<BaseExercise> {
        val todayReview = RetrofitClient.api.getExercisesIds(10, 0).exercisesIds
        val response = RetrofitClient.api.getExercises(listOf(1, 2, 3))

        return response.data.map { dto ->
            BaseExercise(
                id = dto.id,
                sentence = dto.content.word,
                options = dto.content.answers,
                correctAnswerIndex = dto.content.correctIdx
            )
        }
    }

    suspend fun getDefinitionExercises(): List<BaseExercise> {
        return getTranslationExercises()
    }

    suspend fun getMatchExercises(): List<MatchExercise> {
        val response = RetrofitClient.api.getExercises(listOf(7, 8))

        val mockExercise = listOf(
            MatchExercise(
                0,
                listOf("morze", "dom", "samochód", "pies"),
                listOf("sea", "house", "car", "dog")
            )
        )

        return mockExercise
    }
}
