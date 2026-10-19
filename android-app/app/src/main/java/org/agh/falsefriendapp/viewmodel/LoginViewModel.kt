package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.FriendRepository
import org.agh.falsefriendapp.data.repository.LoginRepository
import org.agh.falsefriendapp.data.repository.StatsRepository
import org.agh.falsefriendapp.data.repository.UserRepository
import org.agh.falsefriendapp.ui.state.LoginUiState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (loginRepository.hasToken()) {
                loadSessionData()
            }
            _state.update {
                it.copy(isCheckingSession = false)
            }
        }
    }

    fun onModeChange(isLoginMode: Boolean) {
        _state.update {
            it.copy(isLoginMode = isLoginMode, error = null)
        }
    }

    fun onEmailChange(value: String) {
        _state.update {
            it.copy(email = value, error = null)
        }
    }

    fun onPasswordChange(value: String) {
        _state.update {
            it.copy(password = value, error = null)
        }
    }

    fun onPasswordRepeatChange(value: String) {
        _state.update {
            it.copy(passwordRepeat = value, error = null)
        }
    }

    fun onSubmit() {
        val current = _state.value
        if (!current.canSubmit()) {
            return
        }

        _state.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            val error = if (current.isLoginMode) {
                loginRepository.login(current.email.trim(), current.password)
            } else {
                loginRepository.register(current.email.trim(), current.password)
            }

            if (error == null) {
                loadSessionData()
            }

            _state.update {
                it.copy(isLoading = false, error = error)
            }
        }
    }

    private suspend fun loadSessionData() {
        userRepository.loadProfile()
        friendRepository.refreshRequestsCount()
        statsRepository.refreshStats()

        if (loginRepository.hasToken()) {
            _state.update {
                it.copy(isLoggedIn = true)
            }
        }
    }
}
