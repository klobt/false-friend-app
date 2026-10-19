package org.agh.falsefriendapp.data.model

data class Friend(
    val id: Int,
    val nickname: String?,
    val avatar: Avatar = Avatar.DEFAULT
)
