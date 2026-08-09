package org.agh.falsefriendapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun FalseFriendAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}

private val LightColors = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryContainer,

    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,

    onPrimary = OnPrimary,
    onPrimaryContainer = OnPrimaryContainer,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,

    outline = Outline,
    error = Error
)
