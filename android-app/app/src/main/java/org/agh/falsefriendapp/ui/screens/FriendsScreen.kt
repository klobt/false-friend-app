package org.agh.falsefriendapp.ui.screens

import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.data.model.AddFriendResult
import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.data.model.Friend
import org.agh.falsefriendapp.ui.components.FriendRow
import org.agh.falsefriendapp.ui.components.ScreenHeader
import org.agh.falsefriendapp.ui.components.SectionCard
import org.agh.falsefriendapp.ui.components.displayName
import org.agh.falsefriendapp.ui.state.FriendsUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.FriendsViewModel

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            LoadingScreen()
        }
        state.loadFailed -> {
            ErrorScreen("Nie udało się pobrać znajomych", onNavigateBack)
        }
        else -> {
            FriendsContent(
                state = state,
                onNavigateBack = onNavigateBack,
                onFriendIdChange = viewModel::onFriendIdChange,
                onAddFriend = viewModel::onAddFriend,
                onRemoveFriend = viewModel::onRemoveFriend
            )
        }
    }
}

@Composable
private fun FriendsContent(
    state: FriendsUiState,
    onNavigateBack: () -> Unit,
    onFriendIdChange: (String) -> Unit,
    onAddFriend: () -> Unit,
    onRemoveFriend: (Int) -> Unit
) {
    var removeId by rememberSaveable { mutableStateOf<Int?>(null) }
    val friendToRemove = state.friends.find { it.id == removeId }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        ScreenHeader("Znajomi", onNavigateBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AddFriendSection(
                state = state,
                onFriendIdChange = onFriendIdChange,
                onAddFriend = onAddFriend
            )

            if (state.actionFailed) {
                Text(
                    text = "Nie udało się wykonać operacji, spróbuj ponownie",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            FriendsSection(
                friends = state.friends,
                onRemoveClick = { removeId = it }
            )
        }
    }

    if (friendToRemove != null) {
        RemoveFriendDialog(
            friend = friendToRemove,
            onConfirm = {
                onRemoveFriend(friendToRemove.id)
                removeId = null
            },
            onDismiss = { removeId = null }
        )
    }
}

@Composable
private fun AddFriendSection(
    state: FriendsUiState,
    onFriendIdChange: (String) -> Unit,
    onAddFriend: () -> Unit
) {
    SectionCard(title = "DODAJ ZNAJOMEGO") {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.friendIdInput,
                onValueChange = onFriendIdChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("ID znajomego") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onAddFriend() })
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.width(96.dp).height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
                else {
                    Button(
                        onClick = onAddFriend,
                        enabled = state.canSubmit(),
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Dodaj",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        if (state.addResult != null) {
            Text(
                text = addFriendMessage(state.addResult),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.addResult.isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }

        if (state.ownId != null) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            OwnIdRow(state.ownId)
        }
    }
}

@Composable
private fun OwnIdRow(ownId: Int) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    val clip = ClipData.newPlainText("ID", ownId.toString())
                    clipboard.setClipEntry(ClipEntry(clip))
                }
            }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Twoje ID: $ownId",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Kopiuj ID",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun FriendsSection(
    friends: List<Friend>,
    onRemoveClick: (Int) -> Unit
) {
    SectionCard(title = "TWOI ZNAJOMI · ${friends.size}") {
        if (friends.isEmpty()) {
            Text(
                text = "Dodaj pierwszą osobę, wpisując jej ID powyżej.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        friends.forEachIndexed { index, friend ->
            if (index > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }
            FriendRow(friend) {
                IconButton(onClick = { onRemoveClick(friend.id) }) {
                    Icon(
                        imageVector = Icons.Default.PersonRemove,
                        contentDescription = "Usuń znajomego",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RemoveFriendDialog(
    friend: Friend,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Usunąć znajomego?") },
        text = { Text("${friend.displayName()} zniknie z listy Twoich znajomych.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Usuń", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

private fun addFriendMessage(result: AddFriendResult): String {
    return when (result) {
        AddFriendResult.REQUEST_SENT -> "Zaproszenie wysłane"
        AddFriendResult.ACCEPTED -> "Jesteście teraz znajomymi"
        AddFriendResult.ALREADY_FRIENDS -> "Jesteście już znajomymi"
        AddFriendResult.OWN_ID -> "To Twoje własne ID"
        AddFriendResult.USER_NOT_FOUND -> "Nie ma użytkownika o takim ID"
        AddFriendResult.NO_CONNECTION -> "Brak połączenia z serwerem"
        AddFriendResult.UNKNOWN -> "Wystąpił błąd, spróbuj ponownie"
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendsContentPreview() {
    FalseFriendAppTheme {
        FriendsContent(
            FriendsUiState(
                ownId = 42,
                friends = listOf(
                    Friend(12, "Kuba", Avatar.GAMES),
                    Friend(31, "Marta", Avatar.MUSIC),
                    Friend(8, "tomek99"),
                    Friend(23, null)
                )
            ),
            {}, {}, {}, {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendsContentEmptyPreview() {
    FalseFriendAppTheme {
        FriendsContent(
            FriendsUiState(
                ownId = 42,
                addResult = AddFriendResult.REQUEST_SENT
            ),
            {}, {}, {}, {}
        )
    }
}
