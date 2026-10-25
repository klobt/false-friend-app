package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.NicknameRequest
import org.agh.falsefriendapp.data.model.network.PublicDataRequest
import org.agh.falsefriendapp.data.model.network.UserDto
import org.agh.falsefriendapp.data.model.network.UserStatsDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("users/me")
    suspend fun getMe(): UserDto

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): UserDto

    @PUT("users/me/nickname")
    suspend fun setNickname(@Body body: NicknameRequest)

    @PUT("users/me")
    suspend fun setPublicData(@Body body: PublicDataRequest)

    @GET("users/me/stats")
    suspend fun getStats(): UserStatsDto
}
