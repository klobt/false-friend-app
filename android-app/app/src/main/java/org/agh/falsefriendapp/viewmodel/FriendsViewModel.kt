package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.AddFriendResult
import org.agh.falsefriendapp.data.repository.FriendRepository
import org.agh.falsefriendapp.ui.state.FriendsUiState
import javax.inject.Inject

private const val TAG = "FriendsViewModel"
private const val MAX_ID_LENGTH = 9

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FriendsUiState(isLoading = true))
    val state = _state.asStateFlow()

    private var isActionRunning = false

    init {
        viewModelScope.launch {
            val loaded = loadFriends()
            _state.update {
                it.copy(isLoading = false, loadFailed = !loaded)
            }
        }
    }

    fun onFriendIdChange(value: String) {
        val digits = value.filter { it.isDigit() }.take(MAX_ID_LENGTH)
        _state.update {
            it.copy(friendIdInput = digits, addResult = null)
        }
    }

    fun onAddFriend() {
        val current = _state.value
        val friendId = current.friendIdInput.toIntOrNull()
        if (!current.canSubmit() || friendId == null) {
            return
        }

        _state.update {
            it.copy(isSending = true, addResult = null, actionFailed = false)
        }

        viewModelScope.launch {
            val result = repository.sendRequest(friendId)
            if (result == AddFriendResult.ACCEPTED) {
                loadFriends()
            }

            val canRetry = result == AddFriendResult.NO_CONNECTION ||
                    result == AddFriendResult.UNKNOWN
            _state.update {
                it.copy(
                    isSending = false,
                    addResult = result,
                    friendIdInput = if (canRetry) it.friendIdInput else ""
                )
            }
        }
    }

    fun onAcceptRequest(userId: Int) {
        runAction { repository.respondToRequest(userId, accept = true) }
    }

    fun onRejectRequest(userId: Int) {
        runAction { repository.respondToRequest(userId, accept = false) }
    }

    fun onRemoveFriend(userId: Int) {
        runAction { repository.removeFriend(userId) }
    }

    private fun runAction(action: suspend () -> Boolean) {
        if (isActionRunning) {
            return
        }
        isActionRunning = true

        _state.update {
            it.copy(actionFailed = false, addResult = null)
        }

        viewModelScope.launch {
            try {
                val succeeded = action()
                val reloaded = loadFriends()
                _state.update {
                    it.copy(actionFailed = !succeeded || !reloaded)
                }
            } finally {
                isActionRunning = false
            }
        }
    }

    private suspend fun loadFriends(): Boolean {
        return try {
            val overview = repository.getOverview()
            _state.update {
                it.copy(
                    ownId = overview.ownId,
                    requests = overview.requests,
                    friends = overview.friends
                )
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load friends", e)
            false
        }
    }
}
