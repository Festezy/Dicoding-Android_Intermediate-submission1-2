package com.ariqa.storyapp.view.addmedia

import androidx.lifecycle.ViewModel
import com.ariqa.storyapp.data.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {

    // Fungsi upload image
    suspend fun uploadImage(
        imageFile: MultipartBody.Part,
        requestBody: RequestBody) = repository.uploadImage(imageFile, requestBody)
}