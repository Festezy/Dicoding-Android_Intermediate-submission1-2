package com.ariqa.storyapp.view.addmedia

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.data.response.ErrorResponse
import com.ariqa.storyapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.http.Multipart
import java.io.File

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {

    init {
        getSession()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    // Fungsi upload image
    fun uploadImage(
        token: String,
        imageFile: MultipartBody.Part,
        requestBody: RequestBody
    ){
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.uploadImage("Bearer $token", imageFile, requestBody)
                Log.d(TAG, "uploadImage sucess: ${successResponse.message}")
            } catch (e: HttpException){
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                Log.d(TAG, "uploadImage fail: ${errorResponse.message}")
            }
        }
    }

    companion object{
        private const val TAG = "AddStoryViewModel"
    }
}