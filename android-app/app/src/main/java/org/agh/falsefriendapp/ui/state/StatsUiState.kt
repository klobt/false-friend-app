package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.DailyAccuracy
import java.time.LocalDate
import kotlin.math.roundToInt

const val STATS_DAYS = 30

data class StatsUiState(
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val today: LocalDate = LocalDate.now(),
    val days: List<DailyAccuracy> = emptyList()
) {
    fun firstDay(): LocalDate {
        return today.minusDays(STATS_DAYS - 1L)
    }

    fun totalSessions(): Int {
        return days.sumOf { it.sessions }
    }

    fun totalAnswers(): Int {
        return days.sumOf { it.answers }
    }

    fun accuracyPercent(): Int? {
        val answers = totalAnswers()
        return if (answers > 0) {
            (days.sumOf { it.correct } * 100f / answers).roundToInt()
        } else {
            null
        }
    }
}
