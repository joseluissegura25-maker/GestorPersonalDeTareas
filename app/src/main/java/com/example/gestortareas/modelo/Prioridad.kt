package com.example.gestortareas.modelo

import androidx.annotation.StringRes
import com.example.gestortareas.R

/** Las tres únicas prioridades que puede tener una tarea. */
enum class Prioridad(@param:StringRes val nombre: Int) {
    BAJA(R.string.prioridad_baja),
    MEDIA(R.string.prioridad_media),
    ALTA(R.string.prioridad_alta)
}
