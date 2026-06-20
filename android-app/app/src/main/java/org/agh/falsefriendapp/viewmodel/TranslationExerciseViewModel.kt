package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.TranslationExercise
import org.agh.falsefriendapp.data.repository.ExerciseRepository

class TranslationExerciseViewModel : BaseExerciseViewModel() {
    private val repository = ExerciseRepository()

    init {
        viewModelScope.launch {
            val jsonString = repository.getTranslationExercises()
            Log.d("JSON", jsonString)
        }
    }

    override val exercises = listOf(
        TranslationExercise("1", "a", 1, listOf("a", "b"))
    )

//    val gson = Gson()
//    val type = object : TypeToken<List<TranslationExercise>>() {}.type
//    override val exercises: List<TranslationExercise> =
//        gson.fromJson(jsonString, type)
}
