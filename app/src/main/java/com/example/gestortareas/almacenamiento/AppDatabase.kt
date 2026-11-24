package com.example.gestortareas.almacenamiento

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gestortareas.modelo.Tarea

@Database(entities = [Tarea::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        // Singleton: toda la aplicación comparte una sola conexión con la base de datos
        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestor_tareas.db"
                ).build().also { instancia = it }
            }
    }
}
