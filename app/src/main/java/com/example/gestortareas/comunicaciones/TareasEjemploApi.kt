package com.example.gestortareas.comunicaciones

import retrofit2.http.GET
import retrofit2.http.Query

interface TareasEjemploApi {

    // GET https://jsonplaceholder.typicode.com/todos?userId=1
    @GET("todos")
    suspend fun obtenerTareas(@Query("userId") idUsuario: Int): List<TareaEjemplo>
}
