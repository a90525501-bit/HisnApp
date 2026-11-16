package com.example.hisnapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = darkColorScheme(
    primary = Color(0xFFC9A24B),
    onPrimary = Color(0xFF03201B),
    primaryContainer = Color(0xFF0A2D26),
    onPrimaryContainer = Color(0xFFEFE6CF),
    secondary = Color(0xFFC9A24B),
    background = Color(0xFF03201B),
    onBackground = Color(0xFFEFE6CF),
    surface = Color(0xFF03201B),
    onSurface = Color(0xFFEFE6CF),
    surfaceVariant = Color(0xFF0A2D26),
    onSurfaceVariant = Color(0xFFEFE6CF)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, content = content)
}
