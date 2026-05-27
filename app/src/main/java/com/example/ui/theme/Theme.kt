package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = DarkCard,
    secondary = AccentBlue,
    background = DarkBg,
    surface = DarkCard,
    onBackground = LightText,
    onSurface = LightText,
    error = ErrorRed,
    onError = Color.White,
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    secondary = DarkBlue,
    background = LightBg,
    surface = Color.White,
    onBackground = DarkText,
    onSurface = DarkText,
    error = ErrorRed,
    onError = Color.White,
    outline = BorderLight
)

@Composable
fun MoneyFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
