package com.ariqa.storyapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.di.Injection
import com.ariqa.storyapp.view.addmedia.AddPhotoActivity
import com.ariqa.storyapp.view.addmedia.AddPhotoViewModel
import com.ariqa.storyapp.view.login.LoginViewModel
import com.ariqa.storyapp.view.main.MainViewModel
import com.ariqa.storyapp.view.signup.SignUpViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) ->{
                SignUpViewModel() as T
            }
            modelClass.isAssignableFrom(AddPhotoViewModel::class.java) ->{
                AddPhotoViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}