package com.smaqu.rcgraphsystem.roomdatabase

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.roomdatabase.dao.FileDao

@Database(entities = arrayOf(FileEntity::class), version = 2, exportSchema = false)
abstract class FileDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

    companion object {
        private var instance: FileDatabase? = null

        fun getInstance(context: Context): FileDatabase? {
            if (instance == null) {
                synchronized(FileDatabase::class) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext,
                                FileDatabase::class.java, "file_database.db")
                                .build()
                    }
                }
            }
            return instance
        }

        fun destraoyInstance() {
            instance = null
        }
    }
}