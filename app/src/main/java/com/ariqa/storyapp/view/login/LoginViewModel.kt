package com.ariqa.storyapp.view.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.data.response.LoginResponse
import com.ariqa.storyapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _responseMessage = MutableStateFlow<String?>(null)
    val responseMessage = _responseMessage

    private val _getToken = MutableStateFlow<String?>("")
    val getToken = _getToken

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    suspend fun login(email: String, password: String){
        try {
            _isLoading.value = true
            val apiService = ApiConfig.getApiService()
            val successResponse = apiService.login(email, password)
            _responseMessage.value = successResponse.message
            _getToken.value = successResponse.loginResult!!.token

            Log.d(TAG, "LoginViewModel successResponse: ${successResponse.message}")
            Log.d(TAG, "LoginViewModel successResult: ${successResponse.loginResult}")
        } catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            _responseMessage.value = errorResponse.message

            Log.d(TAG, "LoginViewModel errorResponse: ${errorResponse.message}")
        }
        _isLoading.value = false
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    companion object{
        private const val TAG = "LoginViewModel"
    }
}