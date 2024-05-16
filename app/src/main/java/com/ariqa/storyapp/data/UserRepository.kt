package com.ariqa.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.data.preference.UserPreference
import com.ariqa.storyapp.data.response.ErrorResponse
import com.ariqa.storyapp.data.response.FileUploadResponse
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.data.response.LoginResponse
import com.ariqa.storyapp.data.response.RegisterResponse
import com.ariqa.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
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
//            val apiService = ApiConfig.getApiService()
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
//            val apiService = ApiConfig.getApiService()
            val successResponse = apiService.register(name, email, password)
            result.value = Result.Success(successResponse)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            result.value = Result.Error(errorResponse.message)
        }
        return result
    }

//    suspend fun getStory(): LiveData<Result<List<ListStoryItem>>> {
//        val result = MediatorLiveData<Result<List<ListStoryItem>>>()
//        result.value = Result.Loading
//        try {
////            val token = runBlocking { userPreference.getSession().first().token }
////            val apiService = ApiConfig.getApiService()
//            val successResponse = apiService.getStories()
//            result.value = Result.Success(successResponse.listStory)
//
//        } catch (e: HttpException) {
//            val errorBody = e.response()?.errorBody()?.string()
//            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
//            result.value = Result.Error(errorResponse.message)
//        }
//        return result
//    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoriesPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun getStoryWithLocation(location: Int): LiveData<Result<List<ListStoryItem>>> {
        val result = MediatorLiveData<Result<List<ListStoryItem>>>()
        result.value = Result.Loading
        try {
            val successResponse = apiService.getStoriesWithLocation(location)
            result.value = Result.Success(successResponse.listStory)

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
    ): LiveData<Result<FileUploadResponse>> {
        val result = MediatorLiveData<Result<FileUploadResponse>>()
        result.value = Result.Loading
        try {
            val successResponse = apiService.uploadImage(imageFile, requestBody)
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
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}