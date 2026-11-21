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
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@Composable
fun BaseButton(
    text: String,
    height: Dp,
    enabled: Boolean,
    onClick: () -> Unit,
    isError: Boolean = false
) {
    val errorDelay = 400.milliseconds

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var isTapped by remember { mutableStateOf(false) }
    val tilt = remember { Animatable(0f) }
    val swell = remember { Animatable(1f) }

    LaunchedEffect(isTapped) {
        if (isTapped) {
            listOf(-6f, 6f, -4f, 4f, 0f).forEach {
                tilt.animateTo(
                    targetValue = it,
                    animationSpec = tween(errorDelay.div(5).toInt(DurationUnit.MILLISECONDS))
                )
            }
        }
    }
    LaunchedEffect(isTapped) {
        if (isTapped) {
            listOf(1.2f, 1f).forEach {
                swell.animateTo(
                    targetValue = it,
                    animationSpec = tween(errorDelay.div(2).toInt(DurationUnit.MILLISECONDS))
                )
            }
        }
    }

    val containerColor by animateColorAsState(
        if (isTapped && isError)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.primary
    )

    Button(
        onClick = {
            scope.launch {
                isTapped = true
                if (isError) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    delay(errorDelay)
                }
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
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
