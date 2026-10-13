package org.agh.falsefriendapp.ui.state

const val MAX_NICKNAME_LENGTH = 32

data class SettingsUiState(
    val nickname: String? = null,
    val isEditingNickname: Boolean = false,
    val nicknameInput: String = "",
    val isSaving: Boolean = false,
    val saveFailed: Boolean = false
) {
    fun canSaveNickname(): Boolean {
        return !isSaving && nicknameInput.isNotBlank()
    }
}
