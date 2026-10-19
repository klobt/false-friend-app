package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.AddFriendResult
import org.agh.falsefriendapp.data.model.Friend

data class FriendsUiState(
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
    val ownId: Int? = null,
    val friendIdInput: String = "",
    val isSending: Boolean = false,
    val addResult: AddFriendResult? = null,
    val actionFailed: Boolean = false,
    val friends: List<Friend> = emptyList()
) {
    fun canSubmit(): Boolean {
        return !isSending && friendIdInput.toIntOrNull() != null
    }
}
