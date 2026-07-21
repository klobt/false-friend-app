package org.agh.falsefriendapp.data.repository

import android.util.Log
import org.agh.falsefriendapp.data.api.RetrofitClient
import org.agh.falsefriendapp.data.model.TranslationExercise

private const val TAG = "ExerciseRepository"

class ExerciseRepository {
    suspend fun getTranslationExercises(): List<TranslationExercise> {
        val ids = listOf(1, 2, 3, 4, 5)

        try {
            val response = RetrofitClient.api.getExercises(ids)
            return response.data.map { dto ->
                TranslationExercise(
                    id = dto.id,
                    sentence = dto.content.word,
                    options = dto.content.answers,
                    correctAnswerIndex = dto.content.correctIdx
                )
            }
        } catch (e: Exception) {
            val msg = "Network error"
            Log.e(TAG, msg, e)
            return emptyList()
        }
    }

    suspend fun getDefinitionExercises(): List<TranslationExercise> {
        return getTranslationExercises()
    }
}
