package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.model.TranslationExercise

class TranslationExerciseViewModel : ViewModel() {
    val exercises = listOf(
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

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    fun onAnswerSelected(selectedIndex: Int) {
        val currentExercise = exercises[_currentIndex.value]
        if (selectedIndex == currentExercise.correctAnswerIndex) {
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        _currentIndex.value++
    }
}
