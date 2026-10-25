package org.agh.falsefriendapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.data.model.Friend
import org.agh.falsefriendapp.data.model.UserStats
import org.agh.falsefriendapp.ui.theme.warning

@Composable
fun FriendRow(
    friend: Friend,
    showStats: Boolean = false,
    actions: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FriendAvatar(friend)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.displayName(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showStats) {
                FriendStats(friend.stats)
            } else {
                Text(
                    text = "ID ${friend.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        actions()
    }
}

@Composable
private fun FriendAvatar(friend: Friend) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = friend.avatar.icon(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun FriendStats(stats: UserStats?) {
    if (stats != null && stats.totalAnswers == 0) {
        Text(
            text = "Brak aktywności",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        val streak = stats?.currentStreak?.toString() ?: NO_VALUE
        val accuracy = accuracyText(stats)
        val answers = stats?.let { answersLabel(it.totalAnswers) } ?: "$NO_VALUE odpowiedzi"
        val description = if (stats != null) {
            "Seria ${daysLabel(stats.currentStreak)}, skuteczność $accuracy, $answers"
        } else {
            "Nie udało się wczytać statystyk"
        }

        Row(
            modifier = Modifier.clearAndSetSemantics { contentDescription = description },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.warning,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "$streak · $accuracy · $answers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun answersLabel(count: Int): String {
    return if (count == 1) "1 odpowiedź" else "$count odpowiedzi"
}

private fun daysLabel(count: Int): String {
    return if (count == 1) "1 dzień" else "$count dni"
}

fun Friend.displayName(): String {
    return nickname?.takeIf { it.isNotBlank() } ?: "Użytkownik $id"
}
