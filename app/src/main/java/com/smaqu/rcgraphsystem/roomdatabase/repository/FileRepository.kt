package com.smaqu.rcgraphsystem.roomdatabase.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.roomdatabase.FileDatabase
import com.smaqu.rcgraphsystem.roomdatabase.dao.FileDao

class FileRepository(application: Application) {
    private var fileDao: FileDao

    init {
        val database: FileDatabase = FileDatabase.getInstance(application)!!
        fileDao = database.fileDao()
    }

    fun insertFile(fileEntity: FileEntity) {
        InsertAsyncTask(fileDao).execute(fileEntity)
    }

    fun updateFile(fileEntity: FileEntity) {
        UpdateAsyncTask(fileDao).execute(fileEntity)
    }

    fun deleteOneFileEntity(fileEntity: FileEntity) {
        DeleteOneFileEntityAsyncTask(fileDao).execute(fileEntity)
    }

    fun getAll(): LiveData<List<FileEntity>> {
        return fileDao.queryAll()
    }

    private class InsertAsyncTask(val fileDao: FileDao) : AsyncTask<FileEntity, Void, Void>() {
        override fun doInBackground(vararg params: FileEntity?): Void? {
            fileDao.insertFile(params[0]!!)
            return null
        }
    }

    private class UpdateAsyncTask(val fileDao: FileDao) : AsyncTask<FileEntity, Void, Void>() {
        override fun doInBackground(vararg params: FileEntity?): Void? {
            fileDao.updateFile(params[0]!!)
            return null
        }
    }

    private class DeleteOneFileEntityAsyncTask(val fileDao: FileDao) : AsyncTask<FileEntity, Void, Void>() {
        override fun doInBackground(vararg params: FileEntity?): Void? {
            fileDao.deleteOneFileEntity(params[0]!!)
            return null
        }
    }
}