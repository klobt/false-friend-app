package org.agh.falsefriendapp.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.agh.falsefriendapp.ui.theme.OVERLAY_ALPHA
import org.agh.falsefriendapp.ui.theme.OVERLAY_FADE_MS
import org.agh.falsefriendapp.ui.theme.success

@Composable
fun FeedbackOverlay(wasSuccess: Boolean?) {
    Crossfade(
        targetState = wasSuccess,
        animationSpec = tween(OVERLAY_FADE_MS),
        label = "feedbackOverlay"
    ) { result ->
        if (result != null) {
            val color = if (result) {
                MaterialTheme.colorScheme.success
            } else {
                MaterialTheme.colorScheme.error
            }

            Box(Modifier.fillMaxSize().background(color.copy(alpha = OVERLAY_ALPHA)))
        }
    }
}
