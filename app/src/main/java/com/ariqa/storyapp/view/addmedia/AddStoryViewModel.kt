package com.ariqa.storyapp.view.addmedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.Result
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.response.ErrorResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {

    private val _uploadResult = MutableStateFlow<Result<ErrorResponse>>(Result.Loading)
    val uploadResult: StateFlow<Result<ErrorResponse>> get() = _uploadResult

    fun uploadImage(
        imageFile: MultipartBody.Part,
        requestBody: RequestBody
    ) {
        viewModelScope.launch {
            val result = repository.uploadImage(imageFile, requestBody)
            result.collect { _uploadResult.value = it }
        }
    }
}