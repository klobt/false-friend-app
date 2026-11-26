package org.agh.falsefriendapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExerciseColors(
    val translationForeground: Color,
    val translationBackground: Color,
    val definitionForeground: Color,
    val definitionBackground: Color,
    val matchForeground: Color,
    val matchBackground: Color
)

private val LightExerciseColors = ExerciseColors(
    translationForeground = TranslationForeground,
    translationBackground = TranslationBackground,
    definitionForeground = DefinitionForeground,
    definitionBackground = DefinitionBackground,
    matchForeground = MatchForeground,
    matchBackground = MatchBackground
)

private val DarkExerciseColors = ExerciseColors(
    translationForeground = TranslationForegroundDark,
    translationBackground = TranslationBackgroundDark,
    definitionForeground = DefinitionForegroundDark,
    definitionBackground = DefinitionBackgroundDark,
    matchForeground = MatchForegroundDark,
    matchBackground = MatchBackgroundDark
)

val LocalExerciseColors = staticCompositionLocalOf {
    LightExerciseColors
}

@Composable
fun FalseFriendAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalExerciseColors provides
                if (darkTheme) DarkExerciseColors else LightExerciseColors
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = Typography,
            content = content
        )
    }
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

private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    primaryContainer = PrimaryContainerDark,
    secondary = SecondaryDark,

    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,

    onPrimary = OnPrimaryDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    onSecondary = OnSecondaryDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = OutlineDark,
    error = ErrorDark,
    onError = OnErrorDark,

    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceTint = PrimaryDark
)

val ColorScheme.success: Color
    get() = Success

val ColorScheme.warning: Color
    get() = Warning

val MaterialTheme.exerciseColors: ExerciseColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExerciseColors.current
