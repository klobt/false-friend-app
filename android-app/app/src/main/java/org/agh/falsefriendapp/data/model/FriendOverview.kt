package org.agh.falsefriendapp.data.model

data class FriendOverview(
    val ownId: Int,
    val requests: List<Friend>,
    val friends: List<Friend>
)
