package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.agh.falsefriendapp.data.model.TranslationExercise
import java.net.HttpURLConnection
import java.net.URL

class ExerciseRepository {
    suspend fun getTranslationExercises(): String {
        return withContext(Dispatchers.IO) {
            val url = URL("http://192.168.8.101:8000/exercises?ids=1&ids=2")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.getInputStream().bufferedReader().use {
                it.readText()
            }
        }
    }

//    fun getDefinitionExercises(size: Int): List<TranslationExercise> {
//        return getTranslationExercises(size)
//    }
}
