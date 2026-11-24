package com.example.gestortareas.interfaz.componentes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.gestortareas.R

@Composable
fun DialogoEliminar(tituloTarea: String, alConfirmar: () -> Unit, alCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text(stringResource(R.string.dialogo_eliminar_titulo)) },
        text = { Text(stringResource(R.string.dialogo_eliminar_texto, tituloTarea)) },
        confirmButton = {
            TextButton(onClick = alConfirmar) { Text(stringResource(R.string.boton_eliminar)) }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text(stringResource(R.string.boton_cancelar)) }
        }
    )
}
