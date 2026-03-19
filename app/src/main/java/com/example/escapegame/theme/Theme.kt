package com.example.escapegame.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CodeNPlayScheme = darkColorScheme(
    primary              = BrandBlue,
    onPrimary            = DarkBackground,
    primaryContainer     = BlueContainer,
    onPrimaryContainer   = OnBlueContainer,

    secondary            = BrandGreen,
    onSecondary          = DarkBackground,
    secondaryContainer   = GreenContainer,
    onSecondaryContainer = OnGreenContainer,

    tertiary             = BrandPink,
    onTertiary           = DarkBackground,
    tertiaryContainer    = PinkContainer,
    onTertiaryContainer  = OnPinkContainer,

    background           = DarkBackground,
    onBackground         = OnDark,

    surface              = DarkSurface,
    onSurface            = OnDark,
    surfaceVariant       = DarkSurfaceVar,
    onSurfaceVariant     = OnDarkMuted,

    error                = ErrorRed,
    onError              = DarkBackground,
    errorContainer       = ErrorContainer,
    onErrorContainer     = OnErrorContainer,

    outline              = OutlineColor,
    outlineVariant       = OutlineVariant,
)

@Composable
fun EscapeGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CodeNPlayScheme,
        typography  = Typography,
        content     = content
    )
}
