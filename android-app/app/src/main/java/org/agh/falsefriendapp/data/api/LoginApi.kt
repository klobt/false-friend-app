package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.ChangePasswordRequest
import org.agh.falsefriendapp.data.model.network.LoginRequest
import org.agh.falsefriendapp.data.model.network.RegisterRequest
import org.agh.falsefriendapp.data.model.network.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface LoginApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): TokenResponse

    @PUT("auth/password")
    suspend fun changePassword(@Body body: ChangePasswordRequest)
}
