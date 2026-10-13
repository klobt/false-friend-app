package org.agh.falsefriendapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BaseExerciseHeader(
    currentStep: Int,
    totalSteps: Int,
    onExit: () -> Unit
) {
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        showExitDialog = true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIconButton(
            icon = Icons.Default.Close,
            contentDescription = "Zakończ ćwiczenie",
            onClick = { showExitDialog = true }
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Pytanie $currentStep z $totalSteps",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))

            val progress = (currentStep.toFloat() - 1) / totalSteps
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                label = "exercise_progress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                gapSize = (-8).dp,
                drawStopIndicator = {}
            )
        }
    }

    if (showExitDialog) {
        ExitExerciseDialog(
            onConfirm = {
                showExitDialog = false
                onExit()
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

@Composable
private fun ExitExerciseDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zakończyć ćwiczenie?") },
        text = { Text("Odpowiedzi z tej sesji nie zostaną zapisane.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Zakończ", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kontynuuj")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
