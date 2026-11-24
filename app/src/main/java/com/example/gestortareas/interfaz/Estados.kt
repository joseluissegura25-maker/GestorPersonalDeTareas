package com.example.gestortareas.interfaz

import androidx.annotation.StringRes
import com.example.gestortareas.comunicaciones.TareaEjemplo
import com.example.gestortareas.modelo.Tarea

/** Estado del listado de tareas personales. */
sealed interface EstadoListado {
    data object Cargando : EstadoListado

    // Todavía no hay ninguna tarea guardada
    data object Vacio : EstadoListado

    // Hay tareas. La lista puede quedar vacía si el filtro oculta todas las completadas
    data class Datos(val tareas: List<Tarea>) : EstadoListado
    data object Error : EstadoListado
}

/** Estado del guardado de una tarea en el formulario. */
sealed interface EstadoGuardado {
    data object Inactivo : EstadoGuardado
    data object EnCurso : EstadoGuardado
    data object Completado : EstadoGuardado
    data class Error(@param:StringRes val mensaje: Int) : EstadoGuardado
}

/** Estado de la consulta al servicio web. */
sealed interface EstadoCarga {
    data object Inicial : EstadoCarga
    data object Cargando : EstadoCarga
    data class Datos(val tareas: List<TareaEjemplo>) : EstadoCarga

    // codigoHttp solo tiene valor cuando el servidor ha respondido con un error
    data class Error(@param:StringRes val mensaje: Int, val codigoHttp: Int? = null) : EstadoCarga
}
