package org.agh.falsefriendapp.data.model

enum class AddFriendResult(val isError: Boolean) {
    REQUEST_SENT(false),
    ACCEPTED(false),
    ALREADY_FRIENDS(true),
    OWN_ID(true),
    USER_NOT_FOUND(true),
    NO_CONNECTION(true),
    UNKNOWN(true)
}
