package com.ariqa.storyapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.data.response.ErrorResponse
import com.ariqa.storyapp.data.response.ListStoryItem
import com.ariqa.storyapp.data.retrofit.ApiConfig
import com.ariqa.storyapp.view.login.LoginViewModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _responseMessage = MutableStateFlow<String?>(null)
    val responseMessage = _responseMessage

    private val _getAllStoriesItem = MutableStateFlow<List<ListStoryItem>>(emptyList())
    val getAllStoriesItem = _getAllStoriesItem

    private val _getToken = MutableLiveData<String?>()
    val getToken = _getToken

    fun getAllStories(token: String){
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.getStories("Bearer $token")
                _getAllStoriesItem.value = successResponse.listStory

                _responseMessage.value = successResponse.message
                Log.d(TAG, "getAllStories success: ${successResponse.message}")
            } catch (e: HttpException){
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)

                _responseMessage.value = errorResponse.message
                Log.d(TAG, "getAllStories error: ${errorResponse.message}")
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    companion object{
        private const val TAG = "MainViewModel"
    }

}