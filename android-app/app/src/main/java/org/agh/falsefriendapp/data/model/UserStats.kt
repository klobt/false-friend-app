package org.agh.falsefriendapp.data.model

import kotlin.math.roundToInt

data class UserStats(
    val currentStreak: Int,
    val totalCorrect: Int,
    val totalAnswers: Int
) {
    fun accuracy(): Float? {
        return if (totalAnswers > 0) {
            totalCorrect.toFloat() / totalAnswers
        } else {
            null
        }
    }

    fun accuracyPercent(): Int? {
        return accuracy()?.let { (it * 100).roundToInt() }
    }
}
