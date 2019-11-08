package com.smaqu.rcgraphsystem.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "file_data")
data class FileEntity(@PrimaryKey(autoGenerate = true) var id: Long?,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "data") var data: String) : Serializable {
    constructor() : this(null, "", "")
}