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
import org.agh.falsefriendapp.data.repository.ExerciseRepository
import org.agh.falsefriendapp.data.repository.FriendRepository
import org.agh.falsefriendapp.data.repository.StatsRepository
import org.agh.falsefriendapp.data.repository.UserRepository
import org.agh.falsefriendapp.ui.state.UserMainUiState
import javax.inject.Inject

@HiltViewModel
class UserMainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
    private val statsRepository: StatsRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        UserMainUiState(
            nickname = userRepository.profile.value?.nickname,
            notificationsCount = friendRepository.requestsCount.value,
            avatar = userRepository.profile.value?.avatar ?: Avatar.DEFAULT,
            stats = statsRepository.stats.value,
            dueCounts = exerciseRepository.dueCounts.value
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
        viewModelScope.launch {
            statsRepository.stats.collect { stats ->
                _state.update {
                    it.copy(stats = stats)
                }
            }
        }
        viewModelScope.launch {
            exerciseRepository.dueCounts.collect { dueCounts ->
                _state.update {
                    it.copy(dueCounts = dueCounts)
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
            statsRepository.refreshStats()
            exerciseRepository.refreshDueCounts()
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
