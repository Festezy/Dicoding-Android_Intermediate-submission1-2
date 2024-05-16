package com.ariqa.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    //    suspend fun getStory()= repository.getStory()
    val stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

//    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}