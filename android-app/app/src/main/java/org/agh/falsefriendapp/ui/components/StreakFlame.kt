package org.agh.falsefriendapp.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.agh.falsefriendapp.ui.theme.*

@Composable
fun StreakFlame(active: Boolean, modifier: Modifier = Modifier) {
    if (active) AnimatedFlame(modifier) else StaticFlame(modifier)
}

@Composable
private fun StaticFlame(modifier: Modifier) {
    Icon(
        imageVector = Icons.Default.LocalFireDepartment,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(20.dp)
    )
}

@Composable
private fun AnimatedFlame(modifier: Modifier) {
    val infinite = rememberInfiniteTransition(label = "flame")

    val wobble by infinite.animateFloat(
        initialValue = -FLAME_WOBBLE_DEGREES,
        targetValue = FLAME_WOBBLE_DEGREES,
        animationSpec = infiniteRepeatable(
            tween(FLAME_WOBBLE_MS, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "wobble"
    )
    val swell by infinite.animateFloat(
        initialValue = 1f,
        targetValue = FLAME_SWELL_SCALE,
        animationSpec = infiniteRepeatable(
            tween(FLAME_SWELL_MS, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "swell"
    )

    Icon(
        imageVector = Icons.Default.LocalFireDepartment,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.warning,
        modifier = modifier
            .size(20.dp)
            .graphicsLayer {
                rotationZ = wobble
                scaleX = swell
                scaleY = swell
                transformOrigin = FLAME_PIVOT
            }
    )
}