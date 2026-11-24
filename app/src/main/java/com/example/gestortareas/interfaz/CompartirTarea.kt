package com.example.gestortareas.interfaz

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.example.gestortareas.R
import com.example.gestortareas.modelo.Tarea

/**
 * Abre el selector de aplicaciones para compartir la tarea como texto.
 * Devuelve false si no hay ninguna aplicación compatible. Que devuelva true solo significa que
 * el selector se ha abierto, no que el usuario haya enviado nada.
 */
fun compartirTarea(context: Context, tarea: Tarea): Boolean {
    val descripcion = tarea.descripcion.ifBlank { context.getString(R.string.sin_descripcion) }
    val texto = context.getString(
        R.string.texto_compartir,
        tarea.titulo,
        descripcion,
        context.getString(tarea.prioridad.nombre)
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }

    // El selector se abre aunque no haya aplicaciones, así que se comprueba antes
    if (intent.resolveActivity(context.packageManager) == null) return false

    return try {
        context.startActivity(
            Intent.createChooser(intent, context.getString(R.string.compartir_con))
        )
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}
