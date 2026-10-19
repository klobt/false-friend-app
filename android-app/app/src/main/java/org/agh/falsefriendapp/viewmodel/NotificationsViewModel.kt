package org.agh.falsefriendapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.FriendRepository
import org.agh.falsefriendapp.ui.state.NotificationsUiState
import javax.inject.Inject

private const val TAG = "NotificationsViewModel"

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationsUiState(isLoading = true))
    val state = _state.asStateFlow()

    private var actionJob: Job? = null

    init {
        viewModelScope.launch {
            val loaded = loadRequests()
            _state.update {
                it.copy(isLoading = false, loadFailed = !loaded)
            }
        }
    }

    fun onAcceptRequest(userId: Int) {
        respondToRequest(userId, accept = true)
    }

    fun onRejectRequest(userId: Int) {
        respondToRequest(userId, accept = false)
    }

    private fun respondToRequest(userId: Int, accept: Boolean) {
        if (actionJob?.isActive == true) {
            return
        }

        _state.update {
            it.copy(actionFailed = false)
        }

        actionJob = viewModelScope.launch {
            val succeeded = friendRepository.respondToRequest(userId, accept)
            val reloaded = loadRequests()
            _state.update {
                it.copy(actionFailed = !succeeded || !reloaded)
            }
        }
    }

    private suspend fun loadRequests(): Boolean {
        return try {
            val requests = friendRepository.getRequests()
            _state.update {
                it.copy(requests = requests)
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load requests", e)
            false
        }
    }
}
