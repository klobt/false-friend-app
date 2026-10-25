package org.agh.falsefriendapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun NoReviewsDialog(onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(true) }
    val close = {
        visible = false
        onDismiss()
    }

    if (visible) {
        AlertDialog(
            onDismissRequest = close,
            title = { Text("Brak powtórek na dziś") },
            text = {
                Text("Wszystkie ćwiczenia z tej kategorii są już powtórzone. Zajrzyj później.")
            },
            confirmButton = {
                TextButton(onClick = close) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
