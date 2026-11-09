package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.api.ExerciseApi
import org.agh.falsefriendapp.data.api.UserApi
import org.agh.falsefriendapp.data.model.DailyAccuracy
import org.agh.falsefriendapp.data.model.SessionSummary
import org.agh.falsefriendapp.data.model.UserStats
import org.agh.falsefriendapp.data.model.network.toSessionSummary
import org.agh.falsefriendapp.data.model.network.toUserStats
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

private const val SESSIONS_PAGE_SIZE = 100

@Singleton
class StatsRepository @Inject constructor(
    private val userApi: UserApi,
    private val exerciseApi: ExerciseApi
) {
    private val _stats = MutableStateFlow<UserStats?>(null)
    val stats = _stats.asStateFlow()

    suspend fun refreshStats() {
        _stats.value = try {
            userApi.getStats().toUserStats()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getDailyAccuracy(
        from: LocalDate,
        to: LocalDate,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<DailyAccuracy> {
        return fetchSessionsSince(from, zone)
            .filter { it.answers > 0 && it.date in from..to }
            .groupBy { it.date }
            .map { (date, sessions) ->
                DailyAccuracy(
                    date = date,
                    sessions = sessions.size,
                    correct = sessions.sumOf { it.correct },
                    answers = sessions.sumOf { it.answers }
                )
            }
            .sortedBy { it.date }
    }

    private suspend fun fetchSessionsSince(from: LocalDate, zone: ZoneId): Collection<SessionSummary> {
        val sessions = mutableMapOf<Int, SessionSummary>()
        var end = exerciseApi.getSessions(limit = 0, offset = 0).total

        while (end > 0) {
            val offset = maxOf(0, end - SESSIONS_PAGE_SIZE)
            val page = exerciseApi.getSessions(limit = end - offset, offset = offset).data
                .map { it.toSessionSummary(zone) }
            page.forEach { sessions[it.id] = it }

            if (page.isEmpty() || page.first().date.isBefore(from)) {
                break
            }
            end = offset
        }

        return sessions.values
    }
}
