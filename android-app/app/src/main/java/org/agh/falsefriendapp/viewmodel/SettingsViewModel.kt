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
import org.agh.falsefriendapp.data.model.ThemeMode
import org.agh.falsefriendapp.data.repository.LoginRepository
import org.agh.falsefriendapp.data.repository.UserRepository
import org.agh.falsefriendapp.data.settings.SettingsStore
import org.agh.falsefriendapp.ui.state.MAX_NICKNAME_LENGTH
import org.agh.falsefriendapp.ui.state.PasswordChangeUiState
import org.agh.falsefriendapp.ui.state.SettingsUiState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository,
    private val settingsStore: SettingsStore
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsUiState(
            nickname = userRepository.profile.value?.nickname,
            isProfileLoaded = userRepository.profile.value != null,
            avatar = userRepository.profile.value?.avatar ?: Avatar.DEFAULT
        )
    )
    val state = _state.asStateFlow()

    private var avatarJob: Job? = null

    init {
        viewModelScope.launch {
            userRepository.profile.collect { profile ->
                _state.update {
                    it.copy(
                        nickname = profile?.nickname,
                        isProfileLoaded = profile != null,
                        avatar = profile?.avatar ?: Avatar.DEFAULT
                    )
                }
            }
        }

        viewModelScope.launch {
            settingsStore.themeMode.collect { mode ->
                _state.update {
                    it.copy(themeMode = mode)
                }
            }
        }
    }

    fun onEditNickname() {
        _state.update {
            it.copy(
                isEditingNickname = true,
                nicknameInput = it.nickname.orEmpty(),
                saveFailed = false
            )
        }
    }

    fun onAvatarSelected(avatar: Avatar) {
        if (avatarJob?.isActive == true || avatar == _state.value.avatar) {
            return
        }

        _state.update {
            it.copy(avatarSaveFailed = false)
        }

        avatarJob = viewModelScope.launch {
            val saved = userRepository.setAvatar(avatar)
            _state.update {
                it.copy(avatarSaveFailed = !saved)
            }
        }
    }

    fun onThemeSelected(mode: ThemeMode) {
        viewModelScope.launch {
            settingsStore.setThemeMode(mode)
        }
    }

    fun onNicknameChange(value: String) {
        _state.update {
            it.copy(nicknameInput = value.take(MAX_NICKNAME_LENGTH), saveFailed = false)
        }
    }

    fun onDismissNickname() {
        _state.update {
            it.copy(isEditingNickname = false)
        }
    }

    fun onSaveNickname() {
        val current = _state.value
        if (!current.canSaveNickname()) {
            return
        }

        _state.update {
            it.copy(isSaving = true, saveFailed = false)
        }

        viewModelScope.launch {
            val saved = userRepository.setNickname(current.nicknameInput.trim())
            _state.update {
                it.copy(
                    isSaving = false,
                    saveFailed = !saved,
                    isEditingNickname = !saved
                )
            }
        }
    }

    fun onEditPassword() {
        _state.update {
            it.copy(passwordChange = PasswordChangeUiState(), passwordChanged = false)
        }
    }

    fun onCurrentPasswordChange(value: String) {
        updatePasswordChange {
            it.copy(currentPassword = value, error = null)
        }
    }

    fun onNewPasswordChange(value: String) {
        updatePasswordChange {
            it.copy(newPassword = value, error = null)
        }
    }

    fun onRepeatPasswordChange(value: String) {
        updatePasswordChange {
            it.copy(repeatPassword = value, error = null)
        }
    }

    fun onDismissPassword() {
        _state.update {
            it.copy(passwordChange = null)
        }
    }

    fun onSavePassword() {
        val passwordChange = _state.value.passwordChange
        if (passwordChange == null || !passwordChange.canSave()) {
            return
        }

        updatePasswordChange {
            it.copy(isSaving = true, error = null)
        }

        viewModelScope.launch {
            val error = loginRepository.changePassword(
                passwordChange.currentPassword,
                passwordChange.newPassword
            )
            _state.update {
                if (error == null) {
                    it.copy(passwordChange = null, passwordChanged = true)
                } else {
                    it.copy(passwordChange = it.passwordChange?.copy(isSaving = false, error = error))
                }
            }
        }
    }

    private fun updatePasswordChange(transform: (PasswordChangeUiState) -> PasswordChangeUiState) {
        _state.update { current ->
            val passwordChange = current.passwordChange ?: return@update current
            current.copy(passwordChange = transform(passwordChange))
        }
    }
}
