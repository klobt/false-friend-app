package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.Friend

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val actionFailed: Boolean = false,
    val requests: List<Friend> = emptyList()
)
