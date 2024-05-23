package com.ariqa.storyapp.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.Result
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel (private val repository: UserRepository) : ViewModel() {
    private val _uploadResult = MutableStateFlow<Result<RegisterResponse>>(Result.Loading)
    val uploadResult: StateFlow<Result<RegisterResponse>> get() = _uploadResult

    fun postSignup(
        name: String, email: String,  password: String
    ){
        viewModelScope.launch {
            val result = repository.signUp(name, email, password)
            result.collect { _uploadResult.value = it }
        }
    }

}
