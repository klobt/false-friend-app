package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.data.model.ThemeMode

const val MAX_NICKNAME_LENGTH = 32

data class SettingsUiState(
    val nickname: String? = null,
    val isProfileLoaded: Boolean = false,
    val avatar: Avatar = Avatar.DEFAULT,
    val avatarSaveFailed: Boolean = false,
    val isEditingNickname: Boolean = false,
    val nicknameInput: String = "",
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.LIGHT
) {
    fun canSaveNickname(): Boolean {
        return !isSaving && nicknameInput.isNotBlank()
    }
}
