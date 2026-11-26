package org.agh.falsefriendapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.agh.falsefriendapp.ui.theme.BUTTON_SWELL_SCALE
import org.agh.falsefriendapp.ui.theme.BUTTON_TILT_ANGLES
import kotlin.time.Duration

@Composable
fun BaseButton(
    text: String,
    height: Dp,
    enabled: Boolean,
    onClick: () -> Unit,
    isError: Boolean = false,
    feedbackDelay: Duration = Duration.ZERO,
    keepColorsWhenDisabled: Boolean = false,
    onTap: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var isTapped by remember { mutableStateOf(false) }
    val tilt = remember { Animatable(0f) }
    val swell = remember { Animatable(1f) }

    LaunchedEffect(isTapped) {
        if (isTapped && isError) {
            BUTTON_TILT_ANGLES.forEach {
                tilt.animateTo(it, tween((feedbackDelay / BUTTON_TILT_ANGLES.size)
                    .inWholeMilliseconds.toInt())
                )
            }
        }
    }
    LaunchedEffect(isTapped) {
        if (isTapped) {
            listOf(BUTTON_SWELL_SCALE, 1f).forEach {
                swell.animateTo(it, tween((feedbackDelay / 2)
                    .inWholeMilliseconds.toInt()))
            }
        }
    }

    val containerColor by animateColorAsState(
        if (isTapped && isError) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.primary
    )
    val colors = if (keepColorsWhenDisabled) {
        ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = containerColor,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

    Button(
        onClick = {
            onTap()
            scope.launch {
                isTapped = true
                if (isError) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(feedbackDelay)
                onClick()
            }
        },
        enabled = enabled,
        modifier = Modifier
            .graphicsLayer {
                rotationZ = tilt.value
                scaleX = swell.value
                scaleY = swell.value
            }
            .width(300.dp)
            .height(height),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
