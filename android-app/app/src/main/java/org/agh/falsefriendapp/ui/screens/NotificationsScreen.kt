package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.agh.falsefriendapp.data.model.Friend
import org.agh.falsefriendapp.ui.components.FriendRow
import org.agh.falsefriendapp.ui.components.ScreenHeader
import org.agh.falsefriendapp.ui.components.SectionCard
import org.agh.falsefriendapp.ui.state.NotificationsUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.ui.theme.success
import org.agh.falsefriendapp.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            LoadingScreen()
        }
        state.loadFailed -> {
            ErrorScreen("Nie udało się pobrać powiadomień", onNavigateBack)
        }
        else -> {
            NotificationsContent(
                state = state,
                onNavigateBack = onNavigateBack,
                onAcceptRequest = viewModel::onAcceptRequest,
                onRejectRequest = viewModel::onRejectRequest
            )
        }
    }
}

@Composable
private fun NotificationsContent(
    state: NotificationsUiState,
    onNavigateBack: () -> Unit,
    onAcceptRequest: (Int) -> Unit,
    onRejectRequest: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        ScreenHeader("Powiadomienia", onNavigateBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (state.actionFailed) {
                Text(
                    text = "Nie udało się wykonać operacji, spróbuj ponownie",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (state.requests.isEmpty()) {
                EmptyNotifications()
            }
            else {
                RequestsSection(
                    requests = state.requests,
                    onAccept = onAcceptRequest,
                    onReject = onRejectRequest
                )
            }
        }
    }
}

@Composable
private fun RequestsSection(
    requests: List<Friend>,
    onAccept: (Int) -> Unit,
    onReject: (Int) -> Unit
) {
    SectionCard(title = "ZAPROSZENIA DO ZNAJOMYCH · ${requests.size}") {
        requests.forEachIndexed { index, request ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
            FriendRow(request) {
                IconButton(onClick = { onAccept(request.id) }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Akceptuj zaproszenie",
                        tint = MaterialTheme.colorScheme.success
                    )
                }
                IconButton(onClick = { onReject(request.id) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Odrzuć zaproszenie",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotifications() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Brak nowych powiadomień",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentPreview() {
    FalseFriendAppTheme {
        NotificationsContent(
            NotificationsUiState(
                requests = listOf(Friend(57, "ola_k"), Friend(23, null))
            ),
            {}, {}, {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsContentEmptyPreview() {
    FalseFriendAppTheme {
        NotificationsContent(NotificationsUiState(), {}, {}, {})
    }
}
