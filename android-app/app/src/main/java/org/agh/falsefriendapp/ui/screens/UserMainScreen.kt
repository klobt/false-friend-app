package org.agh.falsefriendapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.ui.components.MenuButton
import org.agh.falsefriendapp.ui.theme.FalseFriendAppTheme

@Composable
fun UserMainScreen(
    onStartTranslation: () -> Unit,
    onStartDefinition: () -> Unit,
    onStartMatch: () -> Unit,
    onStartSettings: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        MenuHeader(onStartSettings)
        ExerciseMenu(
            onStartTranslation = onStartTranslation,
            onStartDefinition = onStartDefinition,
            onStartMatch = onStartMatch
        )
    }
}

@Composable
private fun MenuHeader(onStartSettings: () -> Unit) {
    Surface(color =  MaterialTheme.colorScheme.primaryContainer) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Witaj z powrotem!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Mateusz",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.TopEnd),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                IconButton(
                    onClick = onStartSettings,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil użytkownika",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseMenu(
    onStartTranslation: () -> Unit,
    onStartDefinition: () -> Unit,
    onStartMatch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
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
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FalseFriendAppTheme {
        UserMainScreen(
            onStartTranslation = {},
            onStartDefinition = {},
            onStartMatch = {},
            onStartSettings = {}
        )
    }
}
