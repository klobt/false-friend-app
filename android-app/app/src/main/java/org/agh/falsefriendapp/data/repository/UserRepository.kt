package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.CancellationException
import org.agh.falsefriendapp.data.api.UserApi
import org.agh.falsefriendapp.data.model.network.NicknameRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getNickname(): String? {
        return userApi.getMe().nickname
    }

    suspend fun setNickname(nickname: String): Boolean {
        return try {
            userApi.setNickname(NicknameRequest(nickname))
            true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }
}
