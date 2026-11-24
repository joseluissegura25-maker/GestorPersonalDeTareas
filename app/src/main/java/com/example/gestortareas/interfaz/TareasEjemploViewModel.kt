package com.example.gestortareas.interfaz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestortareas.R
import com.example.gestortareas.comunicaciones.ClienteRetrofit
import com.google.gson.JsonParseException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/** Pide las tareas de ejemplo al servidor y guarda en qué punto está la petición. */
class TareasEjemploViewModel : ViewModel() {

    private val _estadoCarga = MutableStateFlow<EstadoCarga>(EstadoCarga.Inicial)
    val estadoCarga: StateFlow<EstadoCarga> = _estadoCarga.asStateFlow()

    fun cargarTareas() {
        // Evita dos peticiones a la vez
        if (_estadoCarga.value == EstadoCarga.Cargando) return
        _estadoCarga.value = EstadoCarga.Cargando

        viewModelScope.launch {
            _estadoCarga.value = try {
                EstadoCarga.Datos(ClienteRetrofit.api.obtenerTareas(ID_USUARIO))
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                EstadoCarga.Error(R.string.error_servidor, e.code())
            } catch (e: JsonParseException) {
                EstadoCarga.Error(R.string.error_formato)
            } catch (e: IOException) {
                EstadoCarga.Error(R.string.error_conexion)
            } catch (e: Exception) {
                EstadoCarga.Error(R.string.error_inesperado)
            }
        }
    }

    private companion object {
        const val ID_USUARIO = 1
    }
}
