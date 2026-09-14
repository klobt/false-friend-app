package org.agh.falsefriendapp.data.model

data class SessionResult(
    val exerciseId: Int,
    val correct: Boolean,
    val timeMs: Long
)
