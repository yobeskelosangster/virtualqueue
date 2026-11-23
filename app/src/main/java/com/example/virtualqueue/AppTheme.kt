package com.example.virtualqueue

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val BrandStart = Color(0xFF0F766E)
val BrandEnd = Color(0xFF4F46E5)

private val LightColors = lightColorScheme(
    primary = BrandStart,
    onPrimary = Color.White,
    secondary = BrandEnd,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF1E1B4B),
    background = Color(0xFFF4F7F9),
    surface = Color.White,
    surfaceVariant = Color(0xFFEEF2F5),
    onSurfaceVariant = Color(0xFF5B6770),
    error = Color(0xFFDC2626)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF2DD4BF),
    onPrimary = Color(0xFF042F2E),
    secondary = Color(0xFF818CF8),
    secondaryContainer = Color(0xFF312E81),
    onSecondaryContainer = Color(0xFFE0E7FF),
    background = Color(0xFF0F1418),
    surface = Color(0xFF1A2126),
    surfaceVariant = Color(0xFF242C32),
    onSurfaceVariant = Color(0xFFAAB4BC),
    error = Color(0xFFF87171)
)

@Composable
fun QueueTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize()) { content() }
        }
    }
}