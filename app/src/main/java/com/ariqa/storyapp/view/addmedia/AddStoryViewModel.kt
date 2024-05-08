package com.ariqa.storyapp.view.addmedia

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserModel

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {

    init {
        getSession()
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}