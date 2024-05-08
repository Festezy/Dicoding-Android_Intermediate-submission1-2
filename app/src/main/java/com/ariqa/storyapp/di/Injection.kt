package com.ariqa.storyapp.di

import android.content.Context
import com.ariqa.storyapp.data.UserRepository
import com.ariqa.storyapp.data.preference.UserPreference
import com.ariqa.storyapp.data.preference.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}