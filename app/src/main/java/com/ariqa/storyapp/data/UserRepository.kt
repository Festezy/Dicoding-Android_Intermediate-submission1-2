package com.ariqa.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.data.preference.UserPreference
import com.ariqa.storyapp.data.response.ErrorResponse
import com.ariqa.storyapp.data.response.FileUploadResponse
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.data.retrofit.ApiConfig
import com.ariqa.storyapp.view.addmedia.AddStoryViewModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference
) {
    val token = runBlocking { userPreference.getSession().first().token }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getStory(): LiveData<Result<List<ListStoryItem>>> {
        val result = MediatorLiveData<Result<List<ListStoryItem>>>()
        result.value = Result.Loading
        try {
            val apiService = ApiConfig.getApiService()
            val successResponse = apiService.getStories("Bearer $token")
            result.value = Result.Success(successResponse.listStory)

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            result.value = Result.Error(errorResponse.message)
        }
        return result
    }

    suspend fun uploadImage(
        imageFile: MultipartBody.Part,
        requestBody: RequestBody
    ): LiveData<Result<FileUploadResponse>> {
        val result = MediatorLiveData<Result<FileUploadResponse>>()
        result.value = Result.Loading
        try {
            val apiService = ApiConfig.getApiService()
            val successResponse = apiService.uploadImage("Bearer $token", imageFile, requestBody)
            result.value = Result.Success(successResponse)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            result.value = Result.Error(errorResponse.toString())
        }
        return result
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}