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
    onSecondary          = MatrixDeepGreen, // Veranderd van MatrixBlack naar MatrixDeepGreen
    secondaryContainer   = DigitalGray,
    onSecondaryContainer = BrandBlue,

    tertiary             = DarkTertiary,
    onTertiary           = MatrixDeepGreen, // Veranderd van MatrixBlack naar MatrixDeepGreen
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
