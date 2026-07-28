package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.delay
import org.agh.falsefriendapp.data.api.RetrofitClient
import org.agh.falsefriendapp.data.model.TranslationExercise
import kotlin.time.Duration.Companion.seconds

class ExerciseRepository {
    suspend fun getTranslationExercises(): List<TranslationExercise> {
        // TODO do usuniecia po prezentacji

        delay(2.seconds)
        val ids = listOf(1, 2, 3, 4, 5)
        val response = RetrofitClient.api.getExercises(ids)

        return response.data.map { dto ->
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
