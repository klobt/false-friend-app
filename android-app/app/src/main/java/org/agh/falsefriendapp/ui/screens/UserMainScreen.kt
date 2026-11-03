package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.data.model.ExerciseType
import org.agh.falsefriendapp.data.model.UserStats
import org.agh.falsefriendapp.ui.components.HeaderIconButton
import org.agh.falsefriendapp.ui.components.MenuButton
import org.agh.falsefriendapp.ui.components.NO_VALUE
import org.agh.falsefriendapp.ui.components.accuracyText
import org.agh.falsefriendapp.ui.components.icon
import org.agh.falsefriendapp.ui.state.UserMainUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.ui.theme.warning
import org.agh.falsefriendapp.viewmodel.UserMainViewModel

private const val MAX_BADGE_COUNT = 9

@Composable
fun UserMainScreen(
    viewModel: UserMainViewModel = hiltViewModel(),
    onStartTranslation: () -> Unit,
    onStartDefinition: () -> Unit,
    onStartMatch: () -> Unit,
    onStartSettings: () -> Unit,
    onStartFriends: () -> Unit,
    onStartNotifications: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    UserMainContent(
        state = state,
        onStartTranslation = onStartTranslation,
        onStartDefinition = onStartDefinition,
        onStartMatch = onStartMatch,
        onStartSettings = onStartSettings,
        onStartFriends = onStartFriends,
        onStartNotifications = onStartNotifications,
        onDismissProfileError = viewModel::onDismissProfileError
    )
}

@Composable
private fun UserMainContent(
    state: UserMainUiState,
    onStartTranslation: () -> Unit,
    onStartDefinition: () -> Unit,
    onStartMatch: () -> Unit,
    onStartSettings: () -> Unit,
    onStartFriends: () -> Unit,
    onStartNotifications: () -> Unit,
    onDismissProfileError: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        MenuHeader(
            nickname = state.nickname,
            avatar = state.avatar,
            stats = state.stats,
            notificationCount = state.notificationsCount,
            onStartNotifications = onStartNotifications,
            onStartSettings = onStartSettings
        )
        Column(modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
        ) {
            ExerciseMenu(
                dueCounts = state.dueCounts,
                onStartTranslation = onStartTranslation,
                onStartDefinition = onStartDefinition,
                onStartMatch = onStartMatch
            )
            Spacer(modifier = Modifier.height(24.dp))
            CommunityMenu(onStartFriends)
        }
    }

    if (state.showProfileError) {
        ProfileErrorDialog(onDismissProfileError)
    }
}

@Composable
private fun ProfileErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nie udało się wczytać profilu") },
        text = {
            Text("Sprawdź połączenie z internetem. Dane zostaną wczytane ponownie po powrocie na ten ekran.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun MenuHeader(
    nickname: String?,
    avatar: Avatar,
    stats: UserStats?,
    notificationCount: Int,
    onStartNotifications: () -> Unit,
    onStartSettings: () -> Unit
) {
    Surface(color =  MaterialTheme.colorScheme.primaryContainer) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Witaj z powrotem!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (nickname != null) {
                        Text(
                            text = nickname,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                NotificationsButton(notificationCount, onStartNotifications)
                Spacer(modifier = Modifier.width(12.dp))
                HeaderIconButton(
                    icon = avatar.icon(),
                    contentDescription = "Profil użytkownika",
                    onClick = onStartSettings
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            StatsStrip(stats)
        }
    }
}

@Composable
private fun StatsStrip(stats: UserStats?) {
    val accuracy = accuracyText(stats)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                value = stats?.currentStreak?.toString() ?: NO_VALUE,
                label = "dni z rzędu",
                icon = Icons.Default.LocalFireDepartment,
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(
                modifier = Modifier.height(32.dp),
                color = MaterialTheme.colorScheme.outline
            )
            StatItem(
                value = accuracy,
                label = "skuteczność",
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(
                modifier = Modifier.height(32.dp),
                color = MaterialTheme.colorScheme.outline
            )
            StatItem(
                value = stats?.totalAnswers?.toString() ?: NO_VALUE,
                label = "odpowiedzi",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.warning,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NotificationsButton(
    count: Int,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (count > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) {
                    Text(if (count > MAX_BADGE_COUNT) "$MAX_BADGE_COUNT+" else count.toString())
                }
            }
        }
    ) {
        HeaderIconButton(
            icon = Icons.Default.Notifications,
            contentDescription = "Powiadomienia",
            onClick = onClick
        )
    }
}

@Composable
private fun ExerciseMenu(
    dueCounts: Map<ExerciseType, Int>,
    onStartTranslation: () -> Unit,
    onStartDefinition: () -> Unit,
    onStartMatch: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ćwiczenia",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = exercisesSubtitle(dueCounts),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton(
                title = "Wybierz tłumaczenie",
                description = "Wybierz poprawne tłumaczenie",
                icon = Icons.Default.Translate,
                badge = dueCounts[ExerciseType.TRANSLATION],
                onClick = onStartTranslation
            )
            MenuButton(
                title = "Wybierz definicję",
                description = "Dopasuj słowo do definicji",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                badge = dueCounts[ExerciseType.DEFINITION],
                onClick = onStartDefinition
            )
            MenuButton(
                title = "Połącz pary",
                description = "Połącz słowa w pary",
                icon = Icons.AutoMirrored.Filled.CompareArrows,
                badge = dueCounts[ExerciseType.MATCH],
                onClick = onStartMatch
            )
        }
    }
}

@Composable
private fun CommunityMenu(onStartFriends: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Społeczność",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MenuButton(
                title = "Znajomi",
                description = "Dodawaj znajomych i przeglądaj profile",
                icon = Icons.Default.Group,
                onClick = onStartFriends
            )
        }
    }
}

private fun exercisesSubtitle(dueCounts: Map<ExerciseType, Int>): String {
    if (dueCounts.size < ExerciseType.entries.size) {
        return "Wybierz rodzaj ćwiczenia i zacznij naukę"
    }
    return "Na dziś: ${reviewsLabel(dueCounts.values.sum())}"
}

private fun reviewsLabel(count: Int): String {
    val word = when {
        count == 1 -> "powtórka"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "powtórki"
        else -> "powtórek"
    }
    return "$count $word"
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FalseFriendAppTheme {
        UserMainContent(
            state = UserMainUiState(
                nickname = "Mateusz",
                notificationsCount = 3,
                avatar = Avatar.PETS,
                stats = UserStats(
                    currentStreak = 7,
                    totalCorrect = 279,
                    totalAnswers = 340
                ),
                dueCounts = mapOf(
                    ExerciseType.TRANSLATION to 12,
                    ExerciseType.DEFINITION to 9,
                    ExerciseType.MATCH to 0
                )
            ),
            onStartTranslation = {},
            onStartDefinition = {},
            onStartMatch = {},
            onStartSettings = {},
            onStartFriends = {},
            onStartNotifications = {},
            onDismissProfileError = {}
        )
    }
}
