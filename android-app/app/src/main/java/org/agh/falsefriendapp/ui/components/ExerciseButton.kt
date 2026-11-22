package org.agh.falsefriendapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.ui.theme.ANSWER_FEEDBACK_DELAY

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
        feedbackDelay = ANSWER_FEEDBACK_DELAY,
        onTap = onTap,
        onClick = onClick
    )
}