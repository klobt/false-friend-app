package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.api.UserApi
import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.data.model.UserProfile
import org.agh.falsefriendapp.data.model.network.NicknameRequest
import org.agh.falsefriendapp.data.model.network.PublicDataDto
import org.agh.falsefriendapp.data.model.network.PublicDataRequest
import org.agh.falsefriendapp.data.model.network.toUserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile = _profile.asStateFlow()
    var profileLoadFailed = false
        private set

    suspend fun loadProfile(): Boolean {
        return try {
            _profile.value = userApi.getMe().toUserProfile()
            profileLoadFailed = false
            true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _profile.value = null
            profileLoadFailed = true
            false
        }
    }

    suspend fun setNickname(nickname: String): Boolean {
        return try {
            userApi.setNickname(NicknameRequest(nickname))

            val current = _profile.value
            if (current != null) {
                _profile.value = current.copy(nickname = nickname)
            }
            else {
                loadProfile()
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }

    suspend fun setAvatar(avatar: Avatar): Boolean {
        return try {
            userApi.setPublicData(PublicDataRequest(PublicDataDto(avatar.key)))

            val current = _profile.value
            if (current != null) {
                _profile.value = current.copy(avatar = avatar)
            }
            else {
                loadProfile()
            }

            true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }
}
