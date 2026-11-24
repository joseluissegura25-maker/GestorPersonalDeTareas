package com.example.gestortareas.interfaz.componentes

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.gestortareas.R
import com.example.gestortareas.modelo.Tarea

/** Una tarea personal con sus datos y sus acciones. */
@Composable
fun TarjetaTarea(
    tarea: Tarea,
    alCambiarCompletada: (Boolean) -> Unit,
    alEditar: () -> Unit,
    alCompartir: () -> Unit,
    alEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val descripcionCheckbox = stringResource(R.string.marcar_completada, tarea.titulo)
    val estado = stringResource(
        if (tarea.completada) R.string.estado_completada else R.string.estado_pendiente
    )

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 4.dp, top = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = alCambiarCompletada,
                modifier = Modifier.semantics { contentDescription = descripcionCheckbox }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = stringResource(
                        R.string.prioridad_de_tarea,
                        stringResource(tarea.prioridad.nombre)
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.estado_de_tarea, estado),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        // FlowRow baja los botones a otra línea si no caben (pantallas pequeñas o letra grande)
        FlowRow(modifier = Modifier.padding(horizontal = 8.dp)) {
            BotonAccion(R.drawable.ic_editar, R.string.boton_editar, alEditar)
            BotonAccion(R.drawable.ic_compartir, R.string.boton_compartir, alCompartir)
            BotonAccion(R.drawable.ic_eliminar, R.string.boton_eliminar, alEliminar)
        }
    }
}

@Composable
private fun BotonAccion(
    @DrawableRes icono: Int,
    @StringRes texto: Int,
    alPulsar: () -> Unit
) {
    TextButton(onClick = alPulsar) {
        Icon(
            painter = painterResource(icono),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Text(text = stringResource(texto), modifier = Modifier.padding(start = 4.dp))
    }
}
