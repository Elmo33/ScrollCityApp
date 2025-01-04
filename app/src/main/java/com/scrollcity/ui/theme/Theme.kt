package com.scrollcity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00FFFF), // Cyan in HEX
    secondary = Color(0xFF0000FF) // Blue in HEX
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF00FF), // Magenta in HEX
    secondary = Color(0xFF00FFFF) // Cyan in HEX
)

@Composable
fun ScrollCityTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
