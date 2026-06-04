package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import org.agh.falsefriendapp.data.model.TranslationExercise

class TranslationExerciseViewModel : ViewModel() {
    private val exercises = listOf(
        TranslationExercise(id = "0", sentence = "I am hungry")
    )


}
