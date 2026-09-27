package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.auth.LoginError

private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s.]+$")

const val MIN_PASSWORD_LENGTH = 4
const val MAX_PASSWORD_LENGTH = 24

data class LoginUiState(
    val isLoginMode: Boolean = true,
    val email: String = "",
    val password: String = "",
    val passwordRepeat: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: LoginError? = null
) {
    fun isPasswordTooShort(): Boolean {
        return password.length < MIN_PASSWORD_LENGTH
    }

    fun isPasswordTooLong(): Boolean {
        return password.length > MAX_PASSWORD_LENGTH
    }

    fun passwordsMatch(): Boolean {
        return password == passwordRepeat
    }

    fun isEmailValid(): Boolean {
        return EMAIL_REGEX.matches(email.trim())
    }

    fun canSubmit(): Boolean {
        if (isLoading || !isEmailValid() || isPasswordTooLong()) {
            return false
        }

        if (isLoginMode) {
            return password.isNotBlank()
        }

        return !isPasswordTooShort() && passwordsMatch()
    }
}
