package org.agh.falsefriendapp.data.model

import java.time.LocalDate

data class DailyAccuracy(
    val date: LocalDate,
    val sessions: Int,
    val correct: Int,
    val answers: Int
) {
    fun percent(): Float {
        return if (answers > 0) {
            correct * 100f / answers
        } else {
            0f
        }
    }
}
