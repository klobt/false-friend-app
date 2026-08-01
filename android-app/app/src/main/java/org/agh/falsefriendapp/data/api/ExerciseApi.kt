package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.ExerciseResponse
import org.agh.falsefriendapp.data.model.network.ExercisesIds
import retrofit2.http.GET
import retrofit2.http.Query

interface ExerciseApi {
    @GET("exercises")
    suspend fun getExercises(
        @Query("ids") ids: List<Int>
    ): ExerciseResponse

    @GET("reviews/today")
    suspend fun getExercisesIds(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ExercisesIds
}
