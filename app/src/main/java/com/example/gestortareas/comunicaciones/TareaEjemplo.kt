package com.example.gestortareas.comunicaciones

import com.google.gson.annotations.SerializedName

/** Tarea recibida de JSONPlaceholder. No se guarda en Room ni se mezcla con las personales. */
data class TareaEjemplo(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val titulo: String,
    @SerializedName("completed") val completada: Boolean
)
