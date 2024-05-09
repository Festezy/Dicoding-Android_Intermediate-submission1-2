package com.ariqa.storyapp.view.addmedia

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {

    private val _response = MutableStateFlow("")
    val response = _response

    init {
        getSession()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    // Fungsi upload image
    suspend fun uploadImage(
        imageFile: MultipartBody.Part,
        requestBody: RequestBody) = repository.uploadImage(imageFile, requestBody)

    companion object{
        private const val TAG = "AddStoryViewModel"
    }
}