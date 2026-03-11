package com.example.escapegame.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CodeNPlayScheme = lightColorScheme(
    primary              = BrandBlue,
    onPrimary            = LightBackground,
    primaryContainer     = BlueContainer,
    onPrimaryContainer   = OnBlueContainer,

    secondary            = BrandGreen,
    onSecondary          = LightBackground,
    secondaryContainer   = GreenContainer,
    onSecondaryContainer = OnGreenContainer,

    tertiary             = BrandPink,
    onTertiary           = LightBackground,
    tertiaryContainer    = PinkContainer,
    onTertiaryContainer  = OnPinkContainer,

    background           = LightBackground,
    onBackground         = OnLight,

    surface              = LightSurface,
    onSurface            = OnLight,
    surfaceVariant       = LightSurfaceVar,
    onSurfaceVariant     = OnLightMuted,

    error                = ErrorRed,
    onError              = LightBackground,
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
