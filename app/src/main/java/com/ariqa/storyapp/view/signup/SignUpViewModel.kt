package com.ariqa.storyapp.view.signup

import androidx.lifecycle.ViewModel
import com.ariqa.storyapp.data.UserRepository

class SignUpViewModel (private val repository: UserRepository) : ViewModel() {
    suspend fun postSignup(
        name: String, email: String,  password: String
    ) = repository.signUp(name, email, password)

    companion object {
        private const val TAG = "SignupViewModel"
    }
}