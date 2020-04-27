package com.amit.android.marsplayimageupload.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.amit.android.marsplayimageupload.model.AppRepository

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository()

    fun getImageList() = repository.getUploadedImageList()
    fun getImageListData(): LiveData<List<String>> = repository.getUploadedImageListData()
    fun uploadImage(filePath: Uri?) = repository.uploadImage(filePath)
    fun getProcessManagerData(): LiveData<String> = repository.getProcessManagerData()
}