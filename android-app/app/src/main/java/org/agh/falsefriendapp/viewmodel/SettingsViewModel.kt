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
import org.agh.falsefriendapp.data.repository.UserRepository
import org.agh.falsefriendapp.ui.state.MAX_NICKNAME_LENGTH
import org.agh.falsefriendapp.ui.state.SettingsUiState
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val nickname = repository.getNickname()
                _state.update {
                    it.copy(nickname = nickname)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load nickname", e)
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

        val nickname = current.nicknameInput.trim()
        _state.update {
            it.copy(isSaving = true, saveFailed = false)
        }

        viewModelScope.launch {
            val saved = repository.setNickname(nickname)
            _state.update {
                it.copy(
                    isSaving = false,
                    saveFailed = !saved,
                    isEditingNickname = !saved,
                    nickname = if (saved) nickname else it.nickname
                )
            }
        }
    }
}
