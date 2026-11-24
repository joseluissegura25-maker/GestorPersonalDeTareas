package com.example.gestortareas.comunicaciones

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Cliente HTTP único de la aplicación. */
object ClienteRetrofit {

    private const val URL_BASE = "https://jsonplaceholder.typicode.com/"

    private val clienteHttp = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(15, TimeUnit.SECONDS)
        .build()

    val api: TareasEjemploApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TareasEjemploApi::class.java)
    }
}
