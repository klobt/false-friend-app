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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.agh.falsefriendapp.ui.components.HeaderIconButton
import org.agh.falsefriendapp.ui.components.MenuButton
import org.agh.falsefriendapp.ui.state.UserMainUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
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
            notificationCount = state.notificationsCount,
            onStartNotifications = onStartNotifications,
            onStartSettings = onStartSettings
        )
        Column(modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
        ) {
            ExerciseMenu(
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
    notificationCount: Int,
    onStartNotifications: () -> Unit,
    onStartSettings: () -> Unit
) {
    Surface(color =  MaterialTheme.colorScheme.primaryContainer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(24.dp)
        ) {
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
                icon = Icons.Default.Person,
                contentDescription = "Profil użytkownika",
                onClick = onStartSettings
            )
        }
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
            text = "Wybierz rodzaj ćwiczenia i zacznij naukę",
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
                description = "Wybierz poprawne tłumaczenie słowa",
                icon = Icons.Default.Translate,
                onClick = onStartTranslation
            )
            MenuButton(
                title = "Wybierz definicję",
                description = "Dopasuj słowo do definicji",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                onClick = onStartDefinition
            )
            MenuButton(
                title = "Połącz pary",
                description = "Połącz słowa w pary",
                icon = Icons.AutoMirrored.Filled.CompareArrows,
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

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    FalseFriendAppTheme {
        UserMainContent(
            state = UserMainUiState(nickname = "Mateusz", notificationsCount = 3),
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
