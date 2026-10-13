package org.agh.falsefriendapp.data.repository

import kotlinx.coroutines.CancellationException
import org.agh.falsefriendapp.data.api.FriendApi
import org.agh.falsefriendapp.data.api.UserApi
import org.agh.falsefriendapp.data.model.AddFriendResult
import org.agh.falsefriendapp.data.model.Friend
import org.agh.falsefriendapp.data.model.FriendOverview
import org.agh.falsefriendapp.data.model.network.FriendActionRequest
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
    suspend fun getOverview(): FriendOverview {
        val ownId = userApi.getMe().id
        val requestIds = friendApi.getRequests().data
        val friendIds = friendApi.getFriends().data
        val nicknames = fetchNicknames((requestIds + friendIds).toSet())

        return FriendOverview(
            ownId = ownId,
            requests = requestIds.map { Friend(it, nicknames[it]) },
            friends = friendIds.map { Friend(it, nicknames[it]) }
        )
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
        } catch (_: Exception) {
            false
        }
    }

    // TODO: usunąć, gdy serwer zacznie zwracać nicki razem z listą znajomych
    private suspend fun fetchNicknames(userIds: Set<Int>): Map<Int, String?> {
        val nicknames = mutableMapOf<Int, String?>()
        var offset = 0

        while (nicknames.size < userIds.size) {
            val page = userApi.getUsers(USERS_PAGE_SIZE, offset).data
            page.filter { it.id in userIds }.forEach { nicknames[it.id] = it.nickname }

            if (page.size < USERS_PAGE_SIZE) {
                break
            }
            offset += USERS_PAGE_SIZE
        }

        return nicknames
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
