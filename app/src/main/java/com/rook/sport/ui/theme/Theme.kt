package com.rook.sport.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ZonaColors = darkColorScheme(
    primary = ZonaGold,
    onPrimary = ZonaBg,
    secondary = ZonaAccent,
    background = ZonaBg,
    onBackground = ZonaText,
    surface = ZonaCard,
    onSurface = ZonaText,
    error = ZonaRed
)

private val ZonaTypography = Typography(
    headlineLarge = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold),
    headlineMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    labelSmall = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
)

@Composable
fun ZonaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ZonaColors,
        typography = ZonaTypography,
        content = content
    )
}
