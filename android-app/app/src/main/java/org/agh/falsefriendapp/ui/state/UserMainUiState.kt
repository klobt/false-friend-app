package org.agh.falsefriendapp.ui.state

data class UserMainUiState(
    val nickname: String? = null,
    val notificationsCount: Int = 0,
    val showProfileError: Boolean = false
)
