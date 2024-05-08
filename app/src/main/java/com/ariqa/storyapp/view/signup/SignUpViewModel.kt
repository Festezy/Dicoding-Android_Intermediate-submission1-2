package com.ariqa.storyapp.view.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.response.RegisterResponse
import com.ariqa.storyapp.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.HttpException

class SignUpViewModel(): ViewModel() {
    private val _responseMessage = MutableStateFlow<String?>("error")
    val responseMessage = _responseMessage

    suspend fun signUp(name: String, email: String, password: String) {
//        _isLoading.value = true
        try {
            val apiService = ApiConfig.getApiService()
            val successResponse = apiService.register(name, email, password)
            Log.d(TAG, "SignUpViewModel successResponse: ${successResponse.message}")

            _responseMessage.value = successResponse.message
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            Log.d(TAG, "SignUpViewModel errorResponse: ${errorResponse.message}")
            _responseMessage.value = errorResponse.message
        }
//        _isLoading.value = false
    }

    companion object {
        private const val TAG = "SignupViewModel"
    }
}