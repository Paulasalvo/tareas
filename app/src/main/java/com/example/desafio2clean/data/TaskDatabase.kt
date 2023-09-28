package com.example.desafio2clean.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.desafio2clean.model.TaskEntity


@Database(entities = [TaskEntity::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private var database: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            if (database == null) {
                synchronized(TaskDatabase::class) {
                    database = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "task_database"
                    ).build()
                }
            }
            return database!!
        }
    }
}