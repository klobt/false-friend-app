package org.agh.falsefriendapp.data.model

import java.time.LocalDate

data class SessionSummary(
    val id: Int,
    val date: LocalDate,
    val correct: Int,
    val answers: Int
)
