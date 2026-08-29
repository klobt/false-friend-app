package org.agh.falsefriendapp.data.repository

import android.util.Log
import org.agh.falsefriendapp.data.api.RetrofitClient
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.data.model.MatchExercise
import org.agh.falsefriendapp.data.model.Session

class ExerciseRepository {
    suspend fun getTranslationExercises(): List<BaseExercise> {
        return getBaseExercises(ExerciseType.TRANSLATION)
    }

    suspend fun getDefinitionExercises(): List<BaseExercise> {
        return getBaseExercises(ExerciseType.DEFINITION)
    }

    suspend fun getMatchExercises(): List<MatchExercise> {
        val type = ExerciseType.MATCH.apiValue
        val todayReview = RetrofitClient.api.getReviews(type, 4, 0).exercisesIds
        val response = RetrofitClient.api.getMatchExercises(todayReview)

        return response.data.map { dto ->
            MatchExercise(
                id = dto.id,
                left = dto.data.left,
                right = dto.data.right
            )
        }
    }

    suspend fun postSession(session: Session) {
        Log.e("Session", "not implemented")
    }

    private suspend fun getBaseExercises(type: ExerciseType): List<BaseExercise> {
        val todayReview = RetrofitClient.api.getReviews(type.apiValue, 10, 0).exercisesIds
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
