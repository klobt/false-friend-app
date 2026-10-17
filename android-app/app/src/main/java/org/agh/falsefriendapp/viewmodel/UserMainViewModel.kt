package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.data.repository.FriendRepository
import org.agh.falsefriendapp.data.repository.UserRepository
import org.agh.falsefriendapp.ui.state.UserMainUiState
import javax.inject.Inject

@HiltViewModel
class UserMainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        UserMainUiState(
            nickname = userRepository.profile.value?.nickname,
            notificationsCount = friendRepository.requestsCount.value,
            avatar = userRepository.profile.value?.avatar ?: Avatar.DEFAULT
        )
    )
    val state = _state.asStateFlow()

    private var refreshJob: Job? = null
    private var profileErrorShown = false

    init {
        viewModelScope.launch {
            userRepository.profile.collect { profile ->
                _state.update {
                    it.copy(
                        nickname = profile?.nickname,
                        avatar = profile?.avatar ?: Avatar.DEFAULT
                    )
                }
            }
        }
        viewModelScope.launch {
            friendRepository.requestsCount.collect { count ->
                _state.update {
                    it.copy(notificationsCount = count)
                }
            }
        }
    }

    fun refresh() {
        if (refreshJob?.isActive == true) {
            return
        }

        refreshJob = viewModelScope.launch {
            if (!profileErrorShown && userRepository.profileLoadFailed) {
                showProfileError()
                return@launch
            }

            if (userRepository.profile.value == null) {
                val loaded = userRepository.loadProfile()
                if (!loaded && !profileErrorShown) {
                    showProfileError()
                }
            }
            friendRepository.refreshRequestsCount()
        }
    }

    fun onDismissProfileError() {
        _state.update {
            it.copy(showProfileError = false)
        }
    }

    private fun showProfileError() {
        profileErrorShown = true
        _state.update {
            it.copy(showProfileError = true)
        }
    }
}
