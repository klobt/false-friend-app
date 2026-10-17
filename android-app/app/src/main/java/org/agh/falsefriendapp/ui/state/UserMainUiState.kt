package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.Avatar

data class UserMainUiState(
    val nickname: String? = null,
    val notificationsCount: Int = 0,
    val showProfileError: Boolean = false,
    val avatar: Avatar = Avatar.DEFAULT
)
