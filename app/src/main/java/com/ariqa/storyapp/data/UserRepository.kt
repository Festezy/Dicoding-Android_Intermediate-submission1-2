package com.ariqa.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ariqa.storyapp.data.paging3.StoriesPagingSource
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.data.preference.UserPreference
import com.ariqa.storyapp.data.response.ErrorResponse
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.data.response.LoginResponse
import com.ariqa.storyapp.data.response.RegisterResponse
import com.ariqa.storyapp.data.retrofit.ApiConfig
import com.ariqa.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(
        email: String, password: String
    ): LiveData<Result<LoginResponse>> {
        val result = MediatorLiveData<Result<LoginResponse>>()
        result.value = Result.Loading
        try {
            val successResponse = apiService.login(email, password)
            result.value = Result.Success(successResponse)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            result.value = Result.Error(errorResponse.message)
        }
        return result
    }

    suspend fun signUp(
        name: String, email: String, password: String
    ): LiveData<Result<RegisterResponse>> {
        val result = MediatorLiveData<Result<RegisterResponse>>()
        result.value = Result.Loading
        try {
            val successResponse = apiService.register(name, email, password)
            result.value = Result.Success(successResponse)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            result.value = Result.Error(errorResponse.message)
        }
        return result
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        // token perlu dipanggil manual agar pertama kali login aplikasi berhasil utk getStories
        // dan menghindari Bad HTTP authentication header format
        val getToken = runBlocking { userPreference.getSession().first().token }
        val apiService = ApiConfig.getApiService(getToken)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoriesPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun getStoryWithLocation(): LiveData<Result<List<ListStoryItem>>>  {
        val result = MediatorLiveData<Result<List<ListStoryItem>>>()
        result.value = Result.Loading
        try {
            // token perlu dipanggil manual agar pertama kali login aplikasi berhasil utk getStories
            // dan menghindari Bad HTTP authentication header format
            val getToken = runBlocking { userPreference.getSession().first().token }
            val apiService = ApiConfig.getApiService(getToken)
            val successResponse = apiService.getStoriesWithLocation()
            if (successResponse.error == false){
                result.value = Result.Success(successResponse.listStory)
            } else {
                result.value = Result.Error(successResponse.error.toString())
            }

        } catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            result.value = Result.Error(errorResponse.message)
        }
        return result
    }

    suspend fun uploadImage(
        imageFile: MultipartBody.Part,
        requestBody: RequestBody
    ): StateFlow<Result<ErrorResponse>> {
        val result = MutableStateFlow<Result<ErrorResponse>>(Result.Loading)
        withContext(Dispatchers.IO){
            try {
                val getToken = runBlocking { userPreference.getSession().first().token }
                val apiService = ApiConfig.getApiService(getToken)
                val successResponse = apiService.uploadImage(imageFile, requestBody)
                result.value = Result.Success(successResponse)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                result.value = Result.Error(errorResponse.toString())
            }
        }

        return result
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}