package com.example.gestortareas.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tarea personal del usuario. Es también la entidad de Room: cada objeto es una fila de la tabla.
 * Room guarda la prioridad como texto (BAJA, MEDIA o ALTA).
 */
@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val prioridad: Prioridad,
    val completada: Boolean = false
)
