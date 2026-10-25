package org.agh.falsefriendapp.ui.components

import org.agh.falsefriendapp.data.model.UserStats

const val NO_VALUE = "—"

fun accuracyText(stats: UserStats?): String {
    return stats?.accuracyPercent()?.let { "$it%" } ?: NO_VALUE
}
