package com.example.gestortareas.interfaz.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gestortareas.R

/** En pantallas grandes el contenido no se estira más de este ancho. */
val ANCHO_MAXIMO_CONTENIDO = 720.dp

/** Título de la barra superior en una sola línea, para que no se corte con la letra grande. */
@Composable
fun TituloBarra(titulo: String) {
    Text(text = titulo, maxLines = 1, overflow = TextOverflow.Ellipsis)
}

/** Barra superior con la flecha de volver. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraConVolver(titulo: String, volverActivado: Boolean = true, alVolver: () -> Unit) {
    TopAppBar(
        title = { TituloBarra(titulo) },
        navigationIcon = {
            IconButton(onClick = alVolver, enabled = volverActivado) {
                Icon(
                    painter = painterResource(R.drawable.ic_volver),
                    contentDescription = stringResource(R.string.boton_volver)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/** Mensaje centrado con un icono. Se usa para listas vacías y errores. */
@Composable
fun MensajeInformativo(texto: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_lista),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

/** Indicador de carga con un texto debajo. */
@Composable
fun IndicadorCarga(texto: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text(texto)
    }
}
