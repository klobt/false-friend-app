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

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    var correctAnswers = 0
        private set

    fun onAnswerSelected(selectedIndex: Int) {
        val currentExercise = exercises[_currentIndex.value]
        if (selectedIndex == currentExercise.correctAnswerIndex) {
            correctAnswers++
        }

        nextQuestion()
    }

    private fun nextQuestion() {
        if (_currentIndex.value < exercises.size - 1) {
            _currentIndex.value++
        }
        else {
            _isFinished.value = true
        }
    }

    companion object {
        private const val DECK_SIZE = 2
    }
}
