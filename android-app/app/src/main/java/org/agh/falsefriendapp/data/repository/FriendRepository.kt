package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.agh.falsefriendapp.data.api.FriendApi
import org.agh.falsefriendapp.data.api.UserApi
import org.agh.falsefriendapp.data.model.AddFriendResult
import org.agh.falsefriendapp.data.model.Friend
import org.agh.falsefriendapp.data.model.FriendOverview
import org.agh.falsefriendapp.data.model.network.FriendActionRequest
import org.agh.falsefriendapp.data.model.network.UserDto
import org.agh.falsefriendapp.data.model.network.toFriend
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val BAD_REQUEST = 400
private const val NOT_FOUND = 404

private const val STATUS_PENDING = "pending"
private const val STATUS_ACCEPTED = "accepted"
private const val STATUS_ALREADY_FRIENDS = "already_friends"

private const val USERS_PAGE_SIZE = 100

@Singleton
class FriendRepository @Inject constructor(
    private val friendApi: FriendApi,
    private val userApi: UserApi
) {
    private val _requestsCount = MutableStateFlow(0)
    val requestsCount = _requestsCount.asStateFlow()

    suspend fun getOverview(): FriendOverview {
        val ownId = userApi.getMe().id
        val friendIds = friendApi.getFriends().data
        val users = fetchUsers(friendIds.toSet())

        return FriendOverview(
            ownId = ownId,
            friends = friendIds.map { toFriend(it, users) }
        )
    }

    suspend fun getRequests(): List<Friend> {
        val requestIds = friendApi.getRequests().data
        _requestsCount.value = requestIds.size
        val users = fetchUsers(requestIds.toSet())

        return requestIds.map { toFriend(it, users) }
    }

    suspend fun refreshRequestsCount() {
        _requestsCount.value = try {
            friendApi.getRequests().data.size
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            0
        }
    }

    suspend fun sendRequest(userId: Int): AddFriendResult {
        return try {
            when (friendApi.sendRequest(userId).status) {
                STATUS_PENDING -> AddFriendResult.REQUEST_SENT
                STATUS_ACCEPTED -> AddFriendResult.ACCEPTED
                STATUS_ALREADY_FRIENDS -> AddFriendResult.ALREADY_FRIENDS
                else -> AddFriendResult.UNKNOWN
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mapToAddFriendResult(e)
        }
    }

    suspend fun respondToRequest(userId: Int, accept: Boolean): Boolean {
        return try {
            friendApi.respondToRequest(userId, FriendActionRequest(accept))
            true
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }

    suspend fun removeFriend(userId: Int): Boolean {
        return try {
            friendApi.removeFriend(userId)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            e.code() == NOT_FOUND
        } catch (_: Exception) {
            false
        }
    }

    // TODO: usunąć, gdy serwer zacznie zwracać nicki i awatary razem z listą znajomych
    private suspend fun fetchUsers(userIds: Set<Int>): Map<Int, UserDto> {
        val users = mutableMapOf<Int, UserDto>()
        var offset = 0

        while (users.size < userIds.size) {
            val page = userApi.getUsers(USERS_PAGE_SIZE, offset).data
            page.filter { it.id in userIds }.forEach { users[it.id] = it }

            if (page.size < USERS_PAGE_SIZE) {
                break
            }
            offset += USERS_PAGE_SIZE
        }

        return users
    }

    private fun toFriend(id: Int, users: Map<Int, UserDto>): Friend {
        return users[id]?.toFriend() ?: Friend(id, null)
    }

    private fun mapToAddFriendResult(e: Throwable): AddFriendResult {
        return when (e) {
            is HttpException -> when (e.code()) {
                BAD_REQUEST -> AddFriendResult.OWN_ID
                NOT_FOUND -> AddFriendResult.USER_NOT_FOUND
                else -> AddFriendResult.UNKNOWN
            }
            is IOException -> AddFriendResult.NO_CONNECTION
            else -> AddFriendResult.UNKNOWN
        }
    }
}
