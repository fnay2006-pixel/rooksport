package com.rook.sport.admin.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// نفس هوية ROOK الفاخرة
val Bg      = Color(0xFF0A0E1A)
val Bg2     = Color(0xFF0F1626)
val Card    = Color(0xFF141D33)
val Card2   = Color(0xFF1A2540)
val Gold     = Color(0xFFF5B50A)
val Gold2    = Color(0xFFFFD25F)
val Accent  = Color(0xFF00E0A4)
val Red     = Color(0xFFFF3B5C)
val TextC   = Color(0xFFEEF2FB)
val Muted   = Color(0xFF8B97B5)
val Line    = Color(0x12FFFFFF)

private val colors = darkColorScheme(
    primary = Gold, onPrimary = Bg, secondary = Accent,
    background = Bg, onBackground = TextC, surface = Card, onSurface = TextC, error = Red
)

@Composable
fun AdminTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, content = content)
}
