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
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _getAllStoriesItem = MutableStateFlow<List<ListStoryItem>>(emptyList())
    val getAllStoriesItem = _getAllStoriesItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    init {
        getSession()
    }

    suspend fun getStory()= repository.getStory()

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