package com.smaqu.rcgraphsystem.roomdatabase.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.smaqu.rcgraphsystem.models.FileEntity

@Dao
interface FileDao {
    @Insert
    fun insertFile(fileEntity: FileEntity)

    @Update
    fun updateFile(fileEntity: FileEntity)

    @Delete
    fun deleteOneFileEntity(fileEntity: FileEntity)

    @Query("SELECT * FROM file_data")
    fun queryAll(): LiveData<List<FileEntity>>
}