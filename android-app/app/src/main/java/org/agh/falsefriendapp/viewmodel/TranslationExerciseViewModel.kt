package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.repository.ExerciseRepository

class TranslationExerciseViewModel : ViewModel() {
    private val repository = ExerciseRepository()
    val exercises = repository.getExercises(DECK_SIZE)

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    fun onAnswerSelected(selectedIndex: Int) {
        val currentExercise = exercises[_currentIndex.value]
        if (selectedIndex == currentExercise.correctAnswerIndex) {
            // poprawna odpowiedz
        }

        if (_currentIndex.value < exercises.size) {
            nextQuestion()
        }
        else {
            // podsumowanie
        }
    }

    private fun nextQuestion() {
        _currentIndex.value++
    }

    companion object {
        private const val DECK_SIZE = 2
    }
}
