package org.agh.falsefriendapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.repository.UserRepository
import org.agh.falsefriendapp.ui.state.MAX_NICKNAME_LENGTH
import org.agh.falsefriendapp.ui.state.SettingsUiState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        SettingsUiState(
            nickname = repository.profile.value?.nickname,
            isProfileLoaded = repository.profile.value != null
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.profile.collect { profile ->
                _state.update {
                    it.copy(nickname = profile?.nickname, isProfileLoaded = profile != null)
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
            val saved = repository.setNickname(current.nicknameInput.trim())
            _state.update {
                it.copy(
                    isSaving = false,
                    saveFailed = !saved,
                    isEditingNickname = !saved
                )
            }
        }
    }
}
