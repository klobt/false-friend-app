package org.agh.falsefriendapp.ui.state

data class MatchExerciseSession(
    val left: List<String>,
    val right: List<MatchOption>
)

data class MatchOption(
    val originalIndex: Int,
    val text: String
)
