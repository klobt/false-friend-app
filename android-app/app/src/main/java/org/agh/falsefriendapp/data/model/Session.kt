package org.agh.falsefriendapp.data.model

data class Session(
    val userId: Int,
    val results: List<SessionResult>
)
