package com.ariqa.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()

    suspend fun login(
        email: String, password: String
    ) = repository.login(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}