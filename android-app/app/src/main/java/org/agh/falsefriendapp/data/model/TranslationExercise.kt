package org.agh.falsefriendapp.data.model

data class TranslationExercise(
    val id: String,
    val sentence: String,
    val correctAnswerIndex: Int,
    val options: List<String>
)
