package com.ariqa.storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.Result
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserModel
import com.ariqa.storyapp.data.response.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _postResult = MutableStateFlow<Result<LoginResponse>>(Result.Loading)
    val postResult: StateFlow<Result<LoginResponse>> get() = _postResult

    fun login(
        email: String, password: String
    ) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.collect{ _postResult.value = it}
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}