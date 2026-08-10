package org.agh.falsefriendapp.data.model

data class BaseExercise(
    val id: Int,
    val sentence: String,
    val correctAnswerIndex: Int,
    val options: List<String>
)
