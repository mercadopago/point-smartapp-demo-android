package com.mercadopago.android.point_smartapp_demo.app.view.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DemoColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    secondary = Color(0xFF625B71),
    background = Color(0xFFFFFBFE),
    surface = Color.White,
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
)

@Composable
internal fun DemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DemoColorScheme, content = content)
}
