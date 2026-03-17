package com.example.escapegame.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MatrixDarkScheme = darkColorScheme(
    primary              = DarkPrimary,
    onPrimary            = OnDarkPrimary,
    primaryContainer     = MatrixDarkGreen,
    onPrimaryContainer   = MatrixGreen,

    secondary            = DarkSecondary,
    onSecondary          = MatrixBlack,
    secondaryContainer   = DigitalGray,
    onSecondaryContainer = BrandBlue,

    tertiary             = DarkTertiary,
    onTertiary           = MatrixBlack,
    tertiaryContainer    = DigitalGray,
    onTertiaryContainer  = BrandYellow,

    background           = DarkBackground,
    onBackground         = DigitalWhite,

    surface              = DarkSurface,
    onSurface            = DigitalWhite,
    surfaceVariant       = MatrixBlack,
    onSurfaceVariant     = MatrixMutedGreen,

    error                = MatrixRed,
    onError              = OnMatrixRed,

    outline              = MatrixMutedGreen,
)

@Composable
fun EscapeGameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MatrixDarkScheme,
        typography  = Typography,
        content     = content
    )
}
