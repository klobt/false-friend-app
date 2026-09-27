package org.agh.falsefriendapp.data.api

import org.agh.falsefriendapp.data.model.network.LoginRequest
import org.agh.falsefriendapp.data.model.network.RegisterRequest
import org.agh.falsefriendapp.data.model.network.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): TokenResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): TokenResponse
}
