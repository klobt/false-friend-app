package org.agh.falsefriendapp.data.repository

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.api.ExerciseApi
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.data.model.MatchExercise
import org.agh.falsefriendapp.data.model.Session
import org.agh.falsefriendapp.data.model.network.toBaseExercise
import org.agh.falsefriendapp.data.model.network.toMatchExercise
import org.agh.falsefriendapp.data.model.network.toRequest
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ExerciseRepository"
private const val BASE_EXERCISE_LIMIT = 10
private const val MATCH_EXERCISE_LIMIT = 3
private const val REVIEW_OFFSET = 0
private const val DUE_COUNT_LIMIT = 1000

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseApi: ExerciseApi,
    private val statsRepository: StatsRepository
) {
    private val _dueCounts = MutableStateFlow<Map<ExerciseType, Int>>(emptyMap())
    val dueCounts = _dueCounts.asStateFlow()

    suspend fun getExercises(type: ExerciseType): List<BaseExercise> {
        val todayReview = fetchReviewIds(type, BASE_EXERCISE_LIMIT)

        if (todayReview.isEmpty()) {
            return emptyList()
        }

        return exerciseApi.getBaseExercises(todayReview).data.map { dto ->
            dto.toBaseExercise()
        }
    }

    suspend fun getMatchExercises(): List<MatchExercise> {
        val todayReview = fetchReviewIds(ExerciseType.MATCH, MATCH_EXERCISE_LIMIT)

        if (todayReview.isEmpty()) {
            return emptyList()
        }

        return exerciseApi.getMatchExercises(todayReview).data.map { dto ->
            dto.toMatchExercise()
        }
    }

    //TODO: zastapic liczba z serwera, gdy /reviews/today zacznie ja zwracac
    suspend fun refreshDueCounts() {
        _dueCounts.value = try {
            ExerciseType.entries.associateWith { type ->
                fetchReviewIds(type, DUE_COUNT_LIMIT).size
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun submitSession(session: Session) {
        uploadScope.launch {
            try {
                exerciseApi.postSession(session.toRequest())
                statsRepository.refreshStats()
                refreshDueCounts()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to post session", e)
            }
        }
    }

    private suspend fun fetchReviewIds(type: ExerciseType, limit: Int): List<Int> {
        return exerciseApi.getReviews(type.apiValue, limit, REVIEW_OFFSET).exercisesIds
    }

    private companion object {
        val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
