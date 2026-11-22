package org.agh.falsefriendapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.agh.falsefriendapp.ui.theme.*

@Composable
fun FeedbackOverlay(wasSuccess: Boolean?) {
    val alpha by animateFloatAsState(
        targetValue = if (wasSuccess == null) 0f else OVERLAY_ALPHA,
        animationSpec = tween(OVERLAY_FADE_MS)
    )
    val color = if (wasSuccess == false) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.success

    Box(Modifier.fillMaxSize().background(color.copy(alpha = alpha)))
}