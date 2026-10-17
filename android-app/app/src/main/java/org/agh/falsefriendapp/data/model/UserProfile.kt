package org.agh.falsefriendapp.data.model

data class UserProfile(
    val id: Int,
    val nickname: String?,
    val avatar: Avatar = Avatar.DEFAULT
)
