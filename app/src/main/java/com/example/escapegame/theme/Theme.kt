package com.example.escapegame.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MissionControlScheme = darkColorScheme(
    primary              = CyanPrimary,
    onPrimary            = DarkBackground,
    primaryContainer     = CyanDark,
    onPrimaryContainer   = CyanLight,

    secondary            = NeonGreen,
    onSecondary          = DarkBackground,
    secondaryContainer   = NeonGreenDark,
    onSecondaryContainer = NeonGreenLight,

    background           = DarkBackground,
    onBackground         = OnDark,

    surface              = DarkSurface,
    onSurface            = OnDark,
    surfaceVariant       = DarkSurfaceVariant,
    onSurfaceVariant     = OnDarkMuted,

    error                = AlertRed,
    onError              = DarkBackground,
    errorContainer       = AlertRedDark,
    onErrorContainer     = AlertRedLight,

    outline              = PanelBorder,
    outlineVariant       = DarkSurfaceVariant,
)

@Composable
fun EscapeGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MissionControlScheme,
        typography  = Typography,
        content     = content
    )
}
