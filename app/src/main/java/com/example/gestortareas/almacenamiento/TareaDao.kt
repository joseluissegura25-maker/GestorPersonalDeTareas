package com.example.gestortareas.almacenamiento

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gestortareas.modelo.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    // Al devolver un Flow, Room emite la lista de nuevo cada vez que cambia la tabla
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun observarTareas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun buscarPorId(id: Long): Tarea?

    @Insert
    suspend fun insertar(tarea: Tarea): Long

    @Update
    suspend fun actualizar(tarea: Tarea): Int

    @Delete
    suspend fun eliminar(tarea: Tarea): Int
}
