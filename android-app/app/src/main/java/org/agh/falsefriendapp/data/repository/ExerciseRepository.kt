package org.agh.falsefriendapp.data.repository

import org.agh.falsefriendapp.data.api.RetrofitClient
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.data.model.MatchExercise

class ExerciseRepository {
    suspend fun getTranslationExercises(): List<BaseExercise> {
        return getBaseExercises("translation")
    }

    suspend fun getDefinitionExercises(): List<BaseExercise> {
        return getBaseExercises("definition")
    }

    suspend fun getMatchExercises(): List<MatchExercise> {
        val todayReview = RetrofitClient.api.getReviews("connect", 4, 0).exercisesIds
        val response = RetrofitClient.api.getMatchExercises(todayReview)

        return response.data.map { dto ->
            MatchExercise(
                id = dto.id,
                left = dto.data.left,
                right = dto.data.right
            )
        }
    }

    private suspend fun getBaseExercises(type: String): List<BaseExercise> {
        val todayReview = RetrofitClient.api.getReviews(type, 10, 0).exercisesIds
        val response = RetrofitClient.api.getBaseExercises(todayReview)

        return response.data.map { dto ->
            BaseExercise(
                id = dto.id,
                sentence = dto.data.word,
                options = dto.data.answers,
                correctAnswerIndex = dto.data.correctIdx
            )
        }
    }
}
