package com.example.gestortareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestortareas.interfaz.GestorTareasApp
import com.example.gestortareas.interfaz.tema.TemaGestorTareas

/** Única actividad de la aplicación: solo carga la interfaz de Compose. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaGestorTareas {
                GestorTareasApp()
            }
        }
    }
}
