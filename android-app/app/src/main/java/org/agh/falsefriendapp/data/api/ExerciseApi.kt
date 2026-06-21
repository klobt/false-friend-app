package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.ExerciseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ExerciseApi {
    @GET("exercises")
    suspend fun getExercises(
        @Query("ids") ids: List<Int>
    ): ExerciseResponse
}
