package com.spotter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.spotter.R


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9800),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF05080D),
    surface = Color(0xFF121212),
    onSurface = Color.White,
    onBackground = Color(0xFFFFFFFF)
)

// Main theme composable
@Composable
fun SpotterTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
