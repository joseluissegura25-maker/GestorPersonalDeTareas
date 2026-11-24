package com.example.gestortareas.interfaz.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gestortareas.R
import com.example.gestortareas.comunicaciones.TareaEjemplo
import com.example.gestortareas.interfaz.EstadoCarga
import com.example.gestortareas.interfaz.componentes.ANCHO_MAXIMO_CONTENIDO
import com.example.gestortareas.interfaz.componentes.BarraConVolver
import com.example.gestortareas.interfaz.componentes.IndicadorCarga

/** Apartado «Tareas de ejemplo»: consulta al servicio web, separada de las tareas personales. */
@Composable
fun PantallaTareasEjemplo(
    estado: EstadoCarga,
    alCargar: () -> Unit,
    alVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            BarraConVolver(
                titulo = stringResource(R.string.titulo_ejemplos),
                alVolver = alVolver
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = ANCHO_MAXIMO_CONTENIDO)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { CabeceraEjemplos(estado, alCargar) }

                when (estado) {
                    EstadoCarga.Inicial ->
                        item { Text(stringResource(R.string.ejemplos_inicial)) }

                    EstadoCarga.Cargando ->
                        item { IndicadorCarga(stringResource(R.string.cargando_ejemplos)) }

                    is EstadoCarga.Error ->
                        item { ErrorEjemplos(estado, alReintentar = alCargar) }

                    is EstadoCarga.Datos ->
                        if (estado.tareas.isEmpty()) {
                            item { Text(stringResource(R.string.ejemplos_vacio)) }
                        } else {
                            item {
                                Text(
                                    stringResource(
                                        R.string.ejemplos_recibidas,
                                        estado.tareas.size
                                    )
                                )
                            }
                            items(estado.tareas, key = { it.id }) { tarea ->
                                TarjetaTareaEjemplo(tarea)
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun CabeceraEjemplos(estado: EstadoCarga, alCargar: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.explicacion_ejemplos))
        Button(
            onClick = alCargar,
            // Deshabilitado durante la petición para que no haya dos a la vez
            enabled = estado != EstadoCarga.Cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.boton_cargar))
        }
    }
}

@Composable
private fun ErrorEjemplos(error: EstadoCarga.Error, alReintentar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (error.codigoHttp != null) {
                stringResource(error.mensaje, error.codigoHttp)
            } else {
                stringResource(error.mensaje)
            },
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = alReintentar, modifier = Modifier.padding(top = 12.dp)) {
            Text(stringResource(R.string.boton_reintentar))
        }
    }
}

@Composable
private fun TarjetaTareaEjemplo(tarea: TareaEjemplo) {
    val estado = stringResource(
        if (tarea.completada) R.string.estado_completada else R.string.estado_pendiente
    )
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.identificador, tarea.id),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.estado_de_tarea, estado),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
