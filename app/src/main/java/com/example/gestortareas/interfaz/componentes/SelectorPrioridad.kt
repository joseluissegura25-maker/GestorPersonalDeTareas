package com.example.gestortareas.interfaz.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gestortareas.R
import com.example.gestortareas.modelo.Prioridad

/** Menú desplegable con las tres prioridades y su mensaje de error debajo. */
@Composable
fun SelectorPrioridad(
    prioridad: Prioridad?,
    error: String?,
    activado: Boolean,
    alElegir: (Prioridad) -> Unit,
    modifier: Modifier = Modifier
) {
    var desplegado by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.campo_prioridad),
            style = MaterialTheme.typography.labelLarge
        )
        Box {
            OutlinedButton(
                onClick = { desplegado = true },
                enabled = activado,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(prioridad?.nombre ?: R.string.elige_prioridad),
                    modifier = Modifier.weight(1f)
                )
                Icon(painterResource(R.drawable.ic_desplegar), contentDescription = null)
            }
            DropdownMenu(expanded = desplegado, onDismissRequest = { desplegado = false }) {
                Prioridad.entries.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(stringResource(opcion.nombre)) },
                        onClick = {
                            alElegir(opcion)
                            desplegado = false
                        }
                    )
                }
            }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
