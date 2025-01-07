package com.spotter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your custom color schemes
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9800), // Change to your desired primary color
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFF2F2F2),
    surface = Color.White,
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9800),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC5),
    background = Color.Black,
    surface = Color(0xFF121212),
    onSurface = Color.White
)

// Main theme composable
@Composable
fun SpotterTheme(
    darkTheme: Boolean = false, // Toggle between dark and light themes
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
