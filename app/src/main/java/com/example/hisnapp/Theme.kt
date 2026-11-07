package com.example.hisnapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColors = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCEBE6),
    onPrimaryContainer = Color(0xFF00201D),
    secondary = Color(0xFFB45309),
    background = Color(0xFFF7FAF9),
    surface = Color(0xFFF7FAF9),
    surfaceVariant = Color(0xFFE3EFEC),
    onSurface = Color(0xFF1B2B28)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, content = content)
}
