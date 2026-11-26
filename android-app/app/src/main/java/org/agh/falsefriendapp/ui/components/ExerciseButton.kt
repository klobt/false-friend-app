package org.agh.falsefriendapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.ui.theme.ANSWER_FEEDBACK_DELAY
import org.agh.falsefriendapp.ui.theme.BUTTON_SWELL_SCALE
import org.agh.falsefriendapp.ui.theme.BUTTON_TILT_ANGLES

@Composable
fun ExerciseButton(
    text: String,
    isCorrectOption: Boolean,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val tilt = remember { Animatable(0f) }
    val swell = remember { Animatable(1f) }
    val feedbackMs = ANSWER_FEEDBACK_DELAY.inWholeMilliseconds.toInt()
    val isWrongAnswer = isSelected && !isCorrectOption

    LaunchedEffect(isSelected) {
        if (isSelected && !isCorrectOption) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            BUTTON_TILT_ANGLES.forEach {
                tilt.animateTo(it, tween(feedbackMs / BUTTON_TILT_ANGLES.size))
            }
        }
    }
    LaunchedEffect(isSelected) {
        if (isSelected) {
            listOf(BUTTON_SWELL_SCALE, 1f).forEach {
                swell.animateTo(it, tween(feedbackMs / 2))
            }
        }
    }

    val containerColor by animateColorAsState(
        if (isWrongAnswer) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.primary
    )
    val contentColor by animateColorAsState(
        if (isWrongAnswer) MaterialTheme.colorScheme.onError
        else MaterialTheme.colorScheme.onPrimary
    )

    BaseButton(
        text = text,
        height = 70.dp,
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.graphicsLayer {
            rotationZ = tilt.value
            scaleX = swell.value
            scaleY = swell.value
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        )
    )
}
