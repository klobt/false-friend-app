package org.agh.falsefriendapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseButton(
    text: String,
    onClick: () -> Unit,
    isAnsweredCorrectly: Boolean
) {
    BaseButton(
        text = text,
        height = 70.dp,
        enabled = true,
        isError = !isAnsweredCorrectly,
        onClick = onClick
    )
}
