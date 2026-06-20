package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.model.TranslationExercise

abstract class BaseExerciseViewModel : ViewModel() {
    abstract val exercises: List<TranslationExercise>

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
}
