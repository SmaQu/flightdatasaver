package com.smaqu.rcgraphsystem.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.smaqu.rcgraphsystem.models.FileEntity
import com.smaqu.rcgraphsystem.roomdatabase.repository.FileRepository

class SavedFilesVM(application: Application) : AndroidViewModel(application) {

    private var repository: FileRepository = FileRepository(application)

    var filesName: LiveData<List<FileEntity>> = MutableLiveData()
        private set

    init {
        filesName = repository.getAll()
    }

    fun deleteFile(fileEntity: FileEntity) {
        repository.deleteOneFileEntity(fileEntity)
    }
}