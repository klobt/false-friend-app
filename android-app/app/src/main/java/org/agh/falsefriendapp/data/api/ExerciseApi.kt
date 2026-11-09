package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.BaseExerciseResponse
import org.agh.falsefriendapp.data.model.network.ExercisesIds
import org.agh.falsefriendapp.data.model.network.MatchExerciseResponse
import org.agh.falsefriendapp.data.model.network.SessionRequest
import org.agh.falsefriendapp.data.model.network.SessionsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ExerciseApi {
    @GET("exercises")
    suspend fun getBaseExercises(
        @Query("ids") ids: List<Int>
    ): BaseExerciseResponse

    @GET("exercises")
    suspend fun getMatchExercises(
        @Query("ids") ids: List<Int>
    ): MatchExerciseResponse

    @GET("reviews/today")
    suspend fun getReviews(
        @Query("type_filter") type: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ExercisesIds

    @GET("sessions")
    suspend fun getSessions(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): SessionsResponse

    @POST("sessions")
    suspend fun postSession(
        @Body session: SessionRequest
    )
}
