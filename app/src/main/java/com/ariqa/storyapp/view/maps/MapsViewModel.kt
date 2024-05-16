package com.ariqa.storyapp.view.maps

import androidx.lifecycle.ViewModel
import com.ariqa.storyapp.data.UserRepository

class MapsViewModel(private val repository: UserRepository): ViewModel() {

    suspend fun getStoryWithLocation()= repository.getStoryWithLocation(1)
}