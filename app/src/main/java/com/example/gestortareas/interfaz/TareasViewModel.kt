package com.example.gestortareas.interfaz

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestortareas.R
import com.example.gestortareas.almacenamiento.AppDatabase
import com.example.gestortareas.modelo.Prioridad
import com.example.gestortareas.modelo.Tarea
import com.example.gestortareas.preferencias.PreferenciasTareas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Comunica Room y DataStore con la interfaz. Sobrevive a los giros de pantalla. */
class TareasViewModel(application: Application) : AndroidViewModel(application) {

    private val tareaDao = AppDatabase.obtener(application).tareaDao()
    private val preferencias = PreferenciasTareas(application)

    val soloPendientes: StateFlow<Boolean> = preferencias.soloPendientes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // Junta las tareas de Room con el filtro de DataStore: si cambia uno de los dos, se recalcula
    val estadoListado: StateFlow<EstadoListado> =
        combine(tareaDao.observarTareas(), preferencias.soloPendientes) { tareas, soloPendientes ->
            if (tareas.isEmpty()) {
                EstadoListado.Vacio
            } else {
                EstadoListado.Datos(
                    if (soloPendientes) tareas.filter { !it.completada } else tareas
                )
            }
        }
            .catch { emit(EstadoListado.Error) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                EstadoListado.Cargando
            )

    private val _estadoGuardado = MutableStateFlow<EstadoGuardado>(EstadoGuardado.Inactivo)
    val estadoGuardado: StateFlow<EstadoGuardado> = _estadoGuardado.asStateFlow()

    // Aviso breve para la parte inferior de la pantalla; null si no hay nada que mostrar
    private val _mensaje = MutableStateFlow<Int?>(null)
    val mensaje: StateFlow<Int?> = _mensaje.asStateFlow()

    /** Se llama al abrir el formulario, para empezar sin restos del guardado anterior. */
    fun prepararFormulario() {
        _estadoGuardado.value = EstadoGuardado.Inactivo
    }

    suspend fun buscarTarea(id: Long): Tarea? = try {
        tareaDao.buscarPorId(id)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    /** Si id es null crea una tarea nueva; si no, modifica la que ya existe. */
    fun guardar(id: Long?, titulo: String, descripcion: String, prioridad: Prioridad) {
        // Evita duplicados: con un guardado en curso o ya terminado se ignoran más pulsaciones
        val estado = _estadoGuardado.value
        if (estado == EstadoGuardado.EnCurso || estado == EstadoGuardado.Completado) return
        _estadoGuardado.value = EstadoGuardado.EnCurso

        viewModelScope.launch {
            try {
                if (id == null) {
                    tareaDao.insertar(
                        Tarea(
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim(),
                            prioridad = prioridad
                        )
                    )
                } else {
                    modificar(id, titulo.trim(), descripcion.trim(), prioridad)
                }
                _estadoGuardado.value = EstadoGuardado.Completado
                _mensaje.value = R.string.mensaje_tarea_guardada
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estadoGuardado.value = EstadoGuardado.Error(R.string.error_guardar)
            }
        }
    }

    // Parte de la tarea guardada para no perder los datos que el formulario no cambia
    private suspend fun modificar(
        id: Long,
        titulo: String,
        descripcion: String,
        prioridad: Prioridad
    ) {
        val tareaGuardada = checkNotNull(tareaDao.buscarPorId(id))
        tareaDao.actualizar(
            tareaGuardada.copy(titulo = titulo, descripcion = descripcion, prioridad = prioridad)
        )
    }

    fun cambiarCompletada(tarea: Tarea, completada: Boolean) =
        ejecutar(mensajeError = R.string.error_actualizar) {
            tareaDao.actualizar(tarea.copy(completada = completada))
        }

    fun eliminar(tarea: Tarea) =
        ejecutar(
            mensajeError = R.string.error_eliminar,
            mensajeCorrecto = R.string.mensaje_tarea_eliminada
        ) {
            tareaDao.eliminar(tarea)
        }

    fun cambiarSoloPendientes(valor: Boolean) =
        ejecutar(mensajeError = R.string.error_preferencia) {
            preferencias.guardarSoloPendientes(valor)
        }

    fun mostrarMensaje(@StringRes mensaje: Int) {
        _mensaje.value = mensaje
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }

    private fun ejecutar(
        @StringRes mensajeError: Int,
        @StringRes mensajeCorrecto: Int? = null,
        operacion: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                operacion()
                if (mensajeCorrecto != null) _mensaje.value = mensajeCorrecto
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = mensajeError
            }
        }
    }
}
