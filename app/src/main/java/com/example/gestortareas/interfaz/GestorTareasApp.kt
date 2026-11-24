package com.example.gestortareas.interfaz

import androidx.activity.compose.BackHandler
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestortareas.R
import com.example.gestortareas.interfaz.pantallas.PantallaFormulario
import com.example.gestortareas.interfaz.pantallas.PantallaListado
import com.example.gestortareas.interfaz.pantallas.PantallaTareasEjemplo

private enum class Pantalla { LISTADO, FORMULARIO, EJEMPLOS }

/** Decide qué pantalla se ve. La navegación es un estado: al cambiarlo, cambia la pantalla. */
@Composable
fun GestorTareasApp(
    tareasViewModel: TareasViewModel = viewModel(),
    ejemplosViewModel: TareasEjemploViewModel = viewModel()
) {
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.LISTADO) }
    // null significa que el formulario crea una tarea nueva
    var idTareaEditada by rememberSaveable { mutableStateOf<Long?>(null) }

    val estadoListado by tareasViewModel.estadoListado.collectAsStateWithLifecycle()
    val soloPendientes by tareasViewModel.soloPendientes.collectAsStateWithLifecycle()
    val estadoGuardado by tareasViewModel.estadoGuardado.collectAsStateWithLifecycle()
    val estadoCarga by ejemplosViewModel.estadoCarga.collectAsStateWithLifecycle()
    val mensaje by tareasViewModel.mensaje.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val avisos = remember { SnackbarHostState() }
    val textoMensaje = mensaje?.let { stringResource(it) }

    LaunchedEffect(textoMensaje) {
        if (textoMensaje != null) {
            avisos.showSnackbar(textoMensaje)
            tareasViewModel.mensajeMostrado()
        }
    }

    val volverAlListado = { pantalla = Pantalla.LISTADO }
    val abrirFormulario = { id: Long? ->
        tareasViewModel.prepararFormulario()
        idTareaEditada = id
        pantalla = Pantalla.FORMULARIO
    }

    // El botón Atrás vuelve al listado, salvo mientras se está guardando
    BackHandler(enabled = pantalla != Pantalla.LISTADO) {
        if (estadoGuardado != EstadoGuardado.EnCurso) volverAlListado()
    }

    when (pantalla) {
        Pantalla.LISTADO -> PantallaListado(
            estado = estadoListado,
            soloPendientes = soloPendientes,
            avisos = avisos,
            alCambiarSoloPendientes = { valor -> tareasViewModel.cambiarSoloPendientes(valor) },
            alCambiarCompletada = { tarea, completada ->
                tareasViewModel.cambiarCompletada(tarea, completada)
            },
            alCrear = { abrirFormulario(null) },
            alEditar = { tarea -> abrirFormulario(tarea.id) },
            alEliminar = { tarea -> tareasViewModel.eliminar(tarea) },
            alCompartir = { tarea ->
                if (!compartirTarea(context, tarea)) {
                    tareasViewModel.mostrarMensaje(R.string.error_sin_aplicacion)
                }
            },
            alAbrirEjemplos = { pantalla = Pantalla.EJEMPLOS }
        )

        Pantalla.FORMULARIO -> PantallaFormulario(
            idTarea = idTareaEditada,
            estadoGuardado = estadoGuardado,
            buscarTarea = { id -> tareasViewModel.buscarTarea(id) },
            alGuardar = { titulo, descripcion, prioridad ->
                tareasViewModel.guardar(idTareaEditada, titulo, descripcion, prioridad)
            },
            alVolver = volverAlListado
        )

        Pantalla.EJEMPLOS -> PantallaTareasEjemplo(
            estado = estadoCarga,
            alCargar = { ejemplosViewModel.cargarTareas() },
            alVolver = volverAlListado
        )
    }
}
