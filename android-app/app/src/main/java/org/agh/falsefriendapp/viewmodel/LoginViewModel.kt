package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.LoginRepository
import org.agh.falsefriendapp.ui.state.LoginUiState
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (repository.hasToken()) {
                _state.update {
                    it.copy(isLoggedIn = true)
                }
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
                repository.login(current.email.trim(), current.password)
            } else {
                repository.register(current.email.trim(), current.password)
            }

            val isLoggedIn = error == null
            _state.update {
                it.copy(
                    isLoading = false,
                    error = error,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }
}
