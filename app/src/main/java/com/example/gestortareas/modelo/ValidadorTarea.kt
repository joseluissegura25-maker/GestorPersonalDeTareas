package com.example.gestortareas.modelo

import androidx.annotation.StringRes
import com.example.gestortareas.R

/** Reglas del formulario. Cada función devuelve el mensaje de error, o null si el dato es válido. */
object ValidadorTarea {

    private const val LONGITUD_MINIMA_TITULO = 3

    @StringRes
    fun errorDeTitulo(titulo: String): Int? {
        val tituloSinEspacios = titulo.trim()
        return when {
            tituloSinEspacios.isEmpty() -> R.string.error_titulo_vacio
            tituloSinEspacios.length < LONGITUD_MINIMA_TITULO -> R.string.error_titulo_corto
            else -> null
        }
    }

    // Prioridad es un enum: si no es null, solo puede ser una de las tres opciones permitidas
    @StringRes
    fun errorDePrioridad(prioridad: Prioridad?): Int? =
        if (prioridad == null) R.string.error_prioridad_vacia else null
}
