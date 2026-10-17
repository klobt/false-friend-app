package org.agh.falsefriendapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.agh.falsefriendapp.BuildConfig
import org.agh.falsefriendapp.data.model.Avatar
import org.agh.falsefriendapp.ui.components.ScreenHeader
import org.agh.falsefriendapp.ui.components.SectionCard
import org.agh.falsefriendapp.ui.components.icon
import org.agh.falsefriendapp.ui.state.SettingsUiState
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme
import org.agh.falsefriendapp.viewmodel.SessionViewModel
import org.agh.falsefriendapp.viewmodel.SettingsViewModel

private const val AVATARS_PER_ROW = 4

@Composable
fun SettingsScreen(
    viewModel: SessionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()
    val state by settingsViewModel.state.collectAsState()

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            onLoggedOut()
        }
    }

    SettingsContent(
        state = state,
        onNavigateHome = onNavigateHome,
        onLogoutClick = viewModel::logout,
        onEditNickname = settingsViewModel::onEditNickname,
        onNicknameChange = settingsViewModel::onNicknameChange,
        onSaveNickname = settingsViewModel::onSaveNickname,
        onDismissNickname = settingsViewModel::onDismissNickname,
        onAvatarSelected = settingsViewModel::onAvatarSelected
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onNavigateHome: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditNickname: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onSaveNickname: () -> Unit,
    onDismissNickname: () -> Unit,
    onAvatarSelected: (Avatar) -> Unit
) {
    var isAvatarPickerOpen by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) {
        ScreenHeader("Ustawienia", onNavigateHome)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            SectionCard(title = "UŻYTKOWNIK") {
                val username = if (state.isProfileLoaded) {
                    state.nickname ?: "Nie ustawiono"
                } else {
                    "Nie wczytano"
                }
                SettingsItem(
                    title = "Nazwa",
                    value =  username,
                    onClick = onEditNickname
                )
                AvatarItem(
                    avatar = state.avatar,
                    expanded = isAvatarPickerOpen,
                    onClick = { isAvatarPickerOpen = !isAvatarPickerOpen }
                )
                AnimatedVisibility(visible = isAvatarPickerOpen) {
                    AvatarPicker(
                        selected = state.avatar,
                        onSelect = { avatar ->
                            isAvatarPickerOpen = false
                            onAvatarSelected(avatar)
                        }
                    )
                }

                if (state.avatarSaveFailed) {
                    Text(
                        text = "Nie udało się zapisać avatara",
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SectionCard(title = "APLIKACJA") {
                val versionText = BuildConfig.VERSION_NAME + " (${BuildConfig.VERSION_CODE})"
                SettingsItem(title = "Wersja", value = versionText)
                SettingsItem(title = "Język", value = "Polski")
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionCard(title = "KONTO") {
                SettingsAction(onClick = onLogoutClick)
            }
        }
    }

    if (state.isEditingNickname) {
        NicknameDialog(
            state = state,
            onNicknameChange = onNicknameChange,
            onSave = onSaveNickname,
            onDismiss = onDismissNickname
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                modifier = Modifier.weight(1f, fill = false),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AvatarItem(
    avatar: Avatar,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Awatar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = avatar.icon(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            val imageVector = if (expanded) {
                Icons.Default.ExpandLess
            } else {
                Icons.Default.ExpandMore
            }
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AvatarPicker(
    selected: Avatar,
    onSelect: (Avatar) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = AVATARS_PER_ROW
    ) {
        Avatar.entries.forEach { avatar ->
            AvatarOption(
                avatar = avatar,
                selected = avatar == selected,
                onClick = { onSelect(avatar) }
            )
        }
    }
}

@Composable
private fun AvatarOption(
    avatar: Avatar,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(background)
            .border(2.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = avatar.icon(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun SettingsAction(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Wyloguj",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun NicknameDialog(
    state: SettingsUiState,
    onNicknameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Twoja nazwa") },
        text = {
            OutlinedTextField(
                value = state.nicknameInput,
                onValueChange = onNicknameChange,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = state.saveFailed,
                supportingText = {
                    if (state.saveFailed) {
                        Text("Nie udało się zapisać, spróbuj ponownie")
                    }
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = state.canSaveNickname()) {
                Text("Zapisz")
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

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    FalseFriendAppTheme {
        SettingsContent(
            SettingsUiState(
                nickname = "Mateusz",
                isProfileLoaded = true
            ),
            {}, {}, {}, {}, {}, {}, {}
        )
    }
}
