package com.example.gestortareas.interfaz.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val coloresClaros = lightColorScheme(
    primary = Color(0xFF00639B),
    primaryContainer = Color(0xFFCEE5FF),
    onPrimaryContainer = Color(0xFF001D33)
)

private val coloresOscuros = darkColorScheme(
    primary = Color(0xFF96CCFF),
    primaryContainer = Color(0xFF004A77),
    onPrimaryContainer = Color(0xFFCEE5FF)
)

@Composable
fun TemaGestorTareas(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) coloresOscuros else coloresClaros,
        content = contenido
    )
}
