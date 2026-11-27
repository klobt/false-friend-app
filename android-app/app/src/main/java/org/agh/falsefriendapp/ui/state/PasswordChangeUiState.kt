package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.auth.LoginError

data class PasswordChangeUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val repeatPassword: String = "",
    val isSaving: Boolean = false,
    val error: LoginError? = null
) {
    fun isNewPasswordTooShort(): Boolean {
        return newPassword.length < MIN_PASSWORD_LENGTH
    }

    fun isNewPasswordTooLong(): Boolean {
        return newPassword.length > MAX_PASSWORD_LENGTH
    }

    fun isSameAsCurrent(): Boolean {
        return newPassword.isNotEmpty() && newPassword == currentPassword
    }

    fun passwordsMatch(): Boolean {
        return newPassword == repeatPassword
    }

    fun canSave(): Boolean {
        return !isSaving &&
            currentPassword.isNotEmpty() &&
            !isNewPasswordTooShort() &&
            !isNewPasswordTooLong() &&
            !isSameAsCurrent() &&
            passwordsMatch()
    }
}
