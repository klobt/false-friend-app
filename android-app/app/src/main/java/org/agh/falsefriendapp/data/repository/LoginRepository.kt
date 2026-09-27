package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.CancellationException
import org.agh.falsefriendapp.data.api.LoginApi
import org.agh.falsefriendapp.data.auth.LoginError
import org.agh.falsefriendapp.data.auth.TokenStore
import org.agh.falsefriendapp.data.model.network.LoginRequest
import org.agh.falsefriendapp.data.model.network.RegisterRequest
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val UNAUTHORIZED = 401
private const val CONFLICT = 409
private const val UNPROCESSABLE = 422

@Singleton
class LoginRepository @Inject constructor(
    private val loginApi: LoginApi,
    private val tokenStore: TokenStore
) {
    suspend fun login(email: String, password: String): LoginError? {
        return try {
            val response = loginApi.login(LoginRequest(email, password))
            tokenStore.save(response.accessToken)
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mapToLoginError(e)
        }
    }

    suspend fun register(email: String, password: String): LoginError? {
        return try {
            val response = loginApi.register(RegisterRequest(email, password))
            tokenStore.save(response.accessToken)
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mapToLoginError(e)
        }
    }

    suspend fun logout() {
        tokenStore.clear()
    }

    suspend fun hasToken(): Boolean {
        return tokenStore.read() != null
    }

    private fun mapToLoginError(e: Throwable): LoginError {
        return when (e) {
            is HttpException -> when (e.code()) {
                UNAUTHORIZED -> LoginError.INVALID_CREDENTIALS
                CONFLICT -> LoginError.EMAIL_TAKEN
                UNPROCESSABLE -> LoginError.INVALID_EMAIL
                else -> LoginError.UNKNOWN
            }
            is IOException -> LoginError.NO_CONNECTION
            else -> LoginError.UNKNOWN
        }
    }
}
