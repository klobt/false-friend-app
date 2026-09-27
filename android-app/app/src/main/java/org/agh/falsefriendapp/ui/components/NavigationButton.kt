package org.agh.falsefriendapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun NavigationButton(
    text: String,
    onClick: () -> Unit
) {
    BaseButton(
        text = text,
        height = 60.dp,
        enabled = true,
        onClick = onClick
    )
}
