package com.example.gestortareas.interfaz.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.gestortareas.R
import com.example.gestortareas.interfaz.EstadoGuardado
import com.example.gestortareas.interfaz.componentes.ANCHO_MAXIMO_CONTENIDO
import com.example.gestortareas.interfaz.componentes.BarraConVolver
import com.example.gestortareas.interfaz.componentes.IndicadorCarga
import com.example.gestortareas.interfaz.componentes.SelectorPrioridad
import com.example.gestortareas.modelo.Prioridad
import com.example.gestortareas.modelo.Tarea
import com.example.gestortareas.modelo.ValidadorTarea

/** Formulario para crear (idTarea es null) o editar una tarea. */
@Composable
fun PantallaFormulario(
    idTarea: Long?,
    estadoGuardado: EstadoGuardado,
    buscarTarea: suspend (Long) -> Tarea?,
    alGuardar: (String, String, Prioridad) -> Unit,
    alVolver: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var prioridad by rememberSaveable { mutableStateOf<Prioridad?>(null) }
    // Los errores se muestran a partir del primer intento de guardar
    var mostrarErrores by rememberSaveable { mutableStateOf(false) }
    // Al editar hay que traer antes los datos actuales, una sola vez
    var datosListos by rememberSaveable { mutableStateOf(idTarea == null) }

    LaunchedEffect(idTarea) {
        if (idTarea != null && !datosListos) {
            buscarTarea(idTarea)?.let { tarea ->
                titulo = tarea.titulo
                descripcion = tarea.descripcion
                prioridad = tarea.prioridad
            }
            datosListos = true
        }
    }

    // Cuando la tarea ya está guardada, se vuelve al listado
    LaunchedEffect(estadoGuardado) {
        if (estadoGuardado == EstadoGuardado.Completado) alVolver()
    }

    val guardando = estadoGuardado == EstadoGuardado.EnCurso
    val tituloPantalla = if (idTarea == null) R.string.titulo_crear else R.string.titulo_editar

    Scaffold(
        topBar = {
            BarraConVolver(
                titulo = stringResource(tituloPantalla),
                volverActivado = !guardando,
                alVolver = alVolver
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // Evita contar dos veces la barra del sistema cuando se abre el teclado
                .consumeWindowInsets(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            if (datosListos) {
                CamposFormulario(
                    titulo = titulo,
                    descripcion = descripcion,
                    prioridad = prioridad,
                    mostrarErrores = mostrarErrores,
                    estadoGuardado = estadoGuardado,
                    alCambiarTitulo = { titulo = it },
                    alCambiarDescripcion = { descripcion = it },
                    alCambiarPrioridad = { prioridad = it },
                    alGuardar = {
                        mostrarErrores = true
                        val prioridadElegida = prioridad
                        if (ValidadorTarea.errorDeTitulo(titulo) == null &&
                            prioridadElegida != null
                        ) {
                            alGuardar(titulo, descripcion, prioridadElegida)
                        }
                    },
                    alCancelar = alVolver
                )
            } else {
                IndicadorCarga(stringResource(R.string.cargando_tareas))
            }
        }
    }
}

@Composable
private fun CamposFormulario(
    titulo: String,
    descripcion: String,
    prioridad: Prioridad?,
    mostrarErrores: Boolean,
    estadoGuardado: EstadoGuardado,
    alCambiarTitulo: (String) -> Unit,
    alCambiarDescripcion: (String) -> Unit,
    alCambiarPrioridad: (Prioridad) -> Unit,
    alGuardar: () -> Unit,
    alCancelar: () -> Unit
) {
    val teclado = LocalSoftwareKeyboardController.current
    val errorTitulo = if (mostrarErrores) ValidadorTarea.errorDeTitulo(titulo) else null
    val errorPrioridad = if (mostrarErrores) ValidadorTarea.errorDePrioridad(prioridad) else null
    // Se puede editar mientras no se esté guardando ni se haya guardado ya
    val activado = estadoGuardado == EstadoGuardado.Inactivo ||
        estadoGuardado is EstadoGuardado.Error
    val guardando = estadoGuardado == EstadoGuardado.EnCurso

    Column(
        modifier = Modifier
            .widthIn(max = ANCHO_MAXIMO_CONTENIDO)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = alCambiarTitulo,
            label = { Text(stringResource(R.string.campo_titulo)) },
            isError = errorTitulo != null,
            supportingText = errorTitulo?.let { { Text(stringResource(it)) } },
            singleLine = true,
            enabled = activado,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = alCambiarDescripcion,
            label = { Text(stringResource(R.string.campo_descripcion)) },
            minLines = 3,
            enabled = activado,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth()
        )
        SelectorPrioridad(
            prioridad = prioridad,
            error = errorPrioridad?.let { stringResource(it) },
            activado = activado,
            alElegir = alCambiarPrioridad
        )
        if (estadoGuardado is EstadoGuardado.Error) {
            Text(
                text = stringResource(estadoGuardado.mensaje),
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = alCancelar,
                enabled = !guardando,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.boton_cancelar))
            }
            Button(
                onClick = {
                    teclado?.hide()
                    alGuardar()
                },
                enabled = activado,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    stringResource(
                        if (guardando) R.string.boton_guardando else R.string.boton_guardar
                    )
                )
            }
        }
    }
}
