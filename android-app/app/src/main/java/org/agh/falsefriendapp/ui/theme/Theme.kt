package org.agh.falsefriendapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
    secondary = Secondary,

    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,

    onPrimary = OnPrimary,
    onPrimaryContainer = OnPrimaryContainer,
    onSecondary = OnSecondary,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,

    outline = Outline,
    error = Error,
    onError = OnError,

    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    inversePrimary = InversePrimary,
    surfaceTint = Primary
)

val ColorScheme.success: Color
    get() = Success

val ColorScheme.warning: Color
    get() = Warning
