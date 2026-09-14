package org.agh.falsefriendapp.data.repository

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.api.RetrofitClient
import org.agh.falsefriendapp.data.model.BaseExercise
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.data.model.MatchExercise
import org.agh.falsefriendapp.data.model.Session
import org.agh.falsefriendapp.data.model.network.toBaseExercise
import org.agh.falsefriendapp.data.model.network.toMatchExercise
import org.agh.falsefriendapp.data.model.network.toRequest

private const val TAG = "ExerciseRepository"
private const val BASE_EXERCISE_LIMIT = 10
private const val MATCH_EXERCISE_LIMIT = 4
private const val REVIEW_OFFSET = 0

class ExerciseRepository {
    suspend fun getExercises(type: ExerciseType): List<BaseExercise> {
        val todayReview = fetchReviewIds(type, BASE_EXERCISE_LIMIT)

        if (todayReview.isEmpty()) {
            return emptyList()
        }

        return RetrofitClient.api.getBaseExercises(todayReview).data.map { dto ->
            dto.toBaseExercise()
        }
    }

    suspend fun getMatchExercises(): List<MatchExercise> {
        val todayReview = fetchReviewIds(ExerciseType.MATCH, MATCH_EXERCISE_LIMIT)

        if (todayReview.isEmpty()) {
            return emptyList()
        }

        return RetrofitClient.api.getMatchExercises(todayReview).data.map { dto ->
            dto.toMatchExercise()
        }
    }

    fun submitSession(session: Session) {
        uploadScope.launch {
            try {
                RetrofitClient.api.postSession(session.toRequest())
            }
            catch (e: CancellationException) {
                throw e
            }
            catch (e: Exception) {
                Log.e(TAG, "Failed to post session", e)
            }
        }
    }

    private suspend fun fetchReviewIds(type: ExerciseType, limit: Int): List<Int> {
        return RetrofitClient.api.getReviews(type.apiValue, limit, REVIEW_OFFSET).exercisesIds
    }

    private companion object {
        val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
