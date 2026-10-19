package org.agh.falsefriendapp.ui.state

import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.data.model.UserStats

data class UserMainUiState(
    val nickname: String? = null,
    val notificationsCount: Int = 0,
    val showProfileError: Boolean = false,
    val avatar: Avatar = Avatar.DEFAULT,
    val stats: UserStats? = null,
    val dueCounts: Map<ExerciseType, Int> = emptyMap()
)
