package com.example.gestortareas.interfaz.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.gestortareas.R
import com.example.gestortareas.interfaz.EstadoListado
import com.example.gestortareas.interfaz.componentes.ANCHO_MAXIMO_CONTENIDO
import com.example.gestortareas.interfaz.componentes.DialogoEliminar
import com.example.gestortareas.interfaz.componentes.IndicadorCarga
import com.example.gestortareas.interfaz.componentes.MensajeInformativo
import com.example.gestortareas.interfaz.componentes.TarjetaTarea
import com.example.gestortareas.interfaz.componentes.TituloBarra
import com.example.gestortareas.modelo.Tarea

/** Pantalla principal: tareas personales, filtro y acceso al resto de apartados. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListado(
    estado: EstadoListado,
    soloPendientes: Boolean,
    avisos: SnackbarHostState,
    alCambiarSoloPendientes: (Boolean) -> Unit,
    alCambiarCompletada: (Tarea, Boolean) -> Unit,
    alCrear: () -> Unit,
    alEditar: (Tarea) -> Unit,
    alEliminar: (Tarea) -> Unit,
    alCompartir: (Tarea) -> Unit,
    alAbrirEjemplos: () -> Unit
) {
    // Tarea pendiente de confirmar su borrado. Se guarda el id para conservarlo al girar
    var idTareaAEliminar by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TituloBarra(stringResource(R.string.titulo_listado)) },
                actions = {
                    TextButton(onClick = alAbrirEjemplos) {
                        Text(stringResource(R.string.titulo_ejemplos))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alCrear,
                // Este botón no anuncia su texto a los lectores de pantalla: lo anuncia el icono
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_anadir),
                        contentDescription = stringResource(R.string.boton_nueva_tarea)
                    )
                },
                text = { Text(stringResource(R.string.boton_nueva_tarea)) }
            )
        },
        snackbarHost = { SnackbarHost(avisos) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.widthIn(max = ANCHO_MAXIMO_CONTENIDO)) {
                FiltroSoloPendientes(soloPendientes, alCambiarSoloPendientes)
                when (estado) {
                    EstadoListado.Cargando ->
                        IndicadorCarga(stringResource(R.string.cargando_tareas))

                    EstadoListado.Vacio ->
                        MensajeInformativo(stringResource(R.string.listado_vacio))

                    EstadoListado.Error ->
                        MensajeInformativo(stringResource(R.string.error_leer))

                    is EstadoListado.Datos ->
                        if (estado.tareas.isEmpty()) {
                            MensajeInformativo(stringResource(R.string.listado_sin_pendientes))
                        } else {
                            ListaTareas(
                                tareas = estado.tareas,
                                alCambiarCompletada = alCambiarCompletada,
                                alEditar = alEditar,
                                alCompartir = alCompartir,
                                alEliminar = { tarea -> idTareaAEliminar = tarea.id }
                            )
                        }
                }
            }
        }
    }

    val tareaAEliminar = (estado as? EstadoListado.Datos)?.tareas
        ?.find { it.id == idTareaAEliminar }
    if (tareaAEliminar != null) {
        DialogoEliminar(
            tituloTarea = tareaAEliminar.titulo,
            alConfirmar = {
                idTareaAEliminar = null
                alEliminar(tareaAEliminar)
            },
            alCancelar = { idTareaAEliminar = null }
        )
    }
}

@Composable
private fun FiltroSoloPendientes(activado: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = activado, role = Role.Switch, onValueChange = alCambiar)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.solo_pendientes), modifier = Modifier.weight(1f))
        // La fila entera responde a la pulsación, por eso el Switch no tiene la suya propia
        Switch(checked = activado, onCheckedChange = null)
    }
}

@Composable
private fun ListaTareas(
    tareas: List<Tarea>,
    alCambiarCompletada: (Tarea, Boolean) -> Unit,
    alEditar: (Tarea) -> Unit,
    alCompartir: (Tarea) -> Unit,
    alEliminar: (Tarea) -> Unit
) {
    val estadoLista = rememberLazyListState()
    val idPrimera = tareas.first().id
    var idPrimeraAnterior by rememberSaveable { mutableStateOf(idPrimera) }

    // Al entrar una tarea nueva arriba del todo, sube la lista para que se vea
    LaunchedEffect(idPrimera) {
        if (idPrimera != idPrimeraAnterior) {
            estadoLista.scrollToItem(0)
            idPrimeraAnterior = idPrimera
        }
    }

    LazyColumn(
        state = estadoLista,
        // Hueco final para que el botón flotante no tape la última tarea
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tareas, key = { it.id }) { tarea ->
            TarjetaTarea(
                tarea = tarea,
                alCambiarCompletada = { completada -> alCambiarCompletada(tarea, completada) },
                alEditar = { alEditar(tarea) },
                alCompartir = { alCompartir(tarea) },
                alEliminar = { alEliminar(tarea) }
            )
        }
    }
}
