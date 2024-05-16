package com.ariqa.storyapp.di

import android.content.Context
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserPreference
import com.ariqa.storyapp.data.preference.dataStore
import com.ariqa.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}