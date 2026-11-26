package org.agh.falsefriendapp.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.ui.theme.BELL_JIGGLE_DEGREES
import org.agh.falsefriendapp.ui.theme.BELL_JIGGLE_STEP_MS
import org.agh.falsefriendapp.ui.theme.BELL_PIVOT
import org.agh.falsefriendapp.ui.theme.BELL_RETURN_MS
import org.agh.falsefriendapp.ui.theme.BELL_SETTLE_MS
import org.agh.falsefriendapp.ui.theme.BELL_SWING_BACK_DEGREES
import org.agh.falsefriendapp.ui.theme.BELL_SWING_DEGREES
import org.agh.falsefriendapp.ui.theme.BELL_SWING_MS

@Composable
fun NotificationBell(active: Boolean) {
    if (active) RingingBell() else StaticBell()
}

@Composable
private fun StaticBell(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.Notifications,
        contentDescription = "Powiadomienia",
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier.size(32.dp)
    )
}

private val bellCycle = keyframes<Float> {
    val step = BELL_JIGGLE_STEP_MS
    val jiggleEnd = BELL_SWING_MS + 4 * step
    val overshootAt = jiggleEnd + BELL_RETURN_MS
    durationMillis = overshootAt + BELL_SETTLE_MS

    0f at 0 using FastOutSlowInEasing
    BELL_SWING_DEGREES at BELL_SWING_MS using FastOutSlowInEasing
    (BELL_SWING_DEGREES + BELL_JIGGLE_DEGREES) at BELL_SWING_MS + step using FastOutSlowInEasing
    (BELL_SWING_DEGREES - BELL_JIGGLE_DEGREES) at BELL_SWING_MS + 2 * step using FastOutSlowInEasing
    (BELL_SWING_DEGREES + BELL_JIGGLE_DEGREES / 2) at BELL_SWING_MS + 3 * step using FastOutSlowInEasing
    BELL_SWING_DEGREES at jiggleEnd using FastOutSlowInEasing
    BELL_SWING_BACK_DEGREES at overshootAt using FastOutSlowInEasing
    0f at durationMillis using FastOutSlowInEasing
}

@Composable
private fun RingingBell() {
    val infinite = rememberInfiniteTransition(label = "bell")
    val swing by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(bellCycle),
        label = "swing"
    )
    StaticBell(
        Modifier.graphicsLayer {
            rotationZ = swing
            transformOrigin = BELL_PIVOT
        }
    )
}
