package org.agh.falsefriendapp.data.repository

import org.agh.falsefriendapp.data.api.RetrofitClient
import org.agh.falsefriendapp.data.model.TranslationExercise

class ExerciseRepository {
    suspend fun getTranslationExercises(): List<TranslationExercise> {
        val todayReview = RetrofitClient.api.getExercisesIds(10, 0).exercisesIds
        val response = RetrofitClient.api.getExercises(listOf(1, 2, 3, 10))

        return response.data.filter { dto -> dto.type == 101 }
            .map { dto ->
            TranslationExercise(
                id = dto.id,
                sentence = dto.content.word,
                options = dto.content.answers,
                correctAnswerIndex = dto.content.correctIdx
            )
        }
    }

    suspend fun getDefinitionExercises(): List<TranslationExercise> {
        return getTranslationExercises()
    }
}
