package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.NicknameRequest
import org.agh.falsefriendapp.data.model.network.UserDto
import org.agh.falsefriendapp.data.model.network.UsersResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserApi {
    @GET("users/me")
    suspend fun getMe(): UserDto

    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): UsersResponse

    @PUT("users/me/nickname")
    suspend fun setNickname(@Body body: NicknameRequest)
}
