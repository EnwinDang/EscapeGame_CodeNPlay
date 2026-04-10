package com.example.escapegame.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CodeNPlayDarkScheme = darkColorScheme(
    primary              = BrandBlue,
    onPrimary            = Color.Black,
    primaryContainer     = Color(0xFF003652),
    onPrimaryContainer   = BrandBlue,

    secondary            = BrandGreen,
    onSecondary          = Color.Black,
    secondaryContainer   = Color(0xFF1A3300),
    onSecondaryContainer = BrandGreen,

    tertiary             = BrandYellow,
    onTertiary           = Color.Black,
    tertiaryContainer    = Color(0xFF2D1F00),
    onTertiaryContainer  = BrandYellow,

    background           = Color(0xFF050A0F),
    onBackground         = Color.White,

    surface              = Color(0xFF0A0F18),
    onSurface            = Color.White,
    surfaceVariant       = Color(0xFF111820),
    onSurfaceVariant     = Color(0xFFAAAAAA),

    error                = MatrixRed,
    onError              = Color.White,

    outline              = BrandBlue.copy(alpha = 0.4f),
)

@Composable
fun EscapeGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CodeNPlayDarkScheme,
        typography  = Typography,
        content     = content
    )
}