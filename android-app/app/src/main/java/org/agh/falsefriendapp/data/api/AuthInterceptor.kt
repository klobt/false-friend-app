package org.agh.falsefriendapp.data.api

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.agh.falsefriendapp.data.auth.SessionEvents
import org.agh.falsefriendapp.data.auth.TokenStore
import javax.inject.Inject

private const val UNAUTHORIZED = 401
private val PUBLIC_PATHS = listOf("auth/login", "auth/register", "auth/verify-email")

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val sessionEvents: SessionEvents
) : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (PUBLIC_PATHS.any { request.url.encodedPath.endsWith(it) }) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenStore.read() }
        val authorized = if (token != null) {
            request.newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            request
        }

        val response = chain.proceed(authorized)
        if (response.code == UNAUTHORIZED && token != null) {
            runBlocking { tokenStore.clear() }
            sessionEvents.emitForcedLogout()
        }

        return response
    }
}
