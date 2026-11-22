package org.agh.falsefriendapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.milliseconds

private val FEEDBACK_DELAY = 400.milliseconds

@Composable
fun ExerciseButton(
    text: String,
    isAnsweredCorrectly: Boolean,
    enabled: Boolean,
    onTap: () -> Unit,
    onClick: () -> Unit
) {
    BaseButton(
        text = text,
        height = 70.dp,
        enabled = enabled,
        isError = !isAnsweredCorrectly,
        feedbackDelay = FEEDBACK_DELAY,
        onTap = onTap,
        onClick = onClick
    )
}