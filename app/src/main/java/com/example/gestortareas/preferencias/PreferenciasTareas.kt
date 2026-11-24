package com.example.gestortareas.preferencias

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.ajustes by preferencesDataStore(name = "ajustes")

/** Lee y escribe la preferencia «Mostrar solo pendientes». */
class PreferenciasTareas(context: Context) {

    private val ajustes = context.applicationContext.ajustes

    // Si no hay nada guardado, el valor inicial es false: mostrar todas las tareas
    val soloPendientes: Flow<Boolean> = ajustes.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferencias -> preferencias[CLAVE_SOLO_PENDIENTES] ?: false }

    suspend fun guardarSoloPendientes(valor: Boolean) {
        ajustes.edit { preferencias -> preferencias[CLAVE_SOLO_PENDIENTES] = valor }
    }

    private companion object {
        val CLAVE_SOLO_PENDIENTES = booleanPreferencesKey("solo_pendientes")
    }
}
