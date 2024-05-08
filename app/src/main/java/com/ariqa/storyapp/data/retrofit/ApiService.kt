package com.ariqa.storyapp.data.retrofit

import com.ariqa.storyapp.data.response.AllStoriesResponse
import com.ariqa.storyapp.data.response.LoginResponse
import com.ariqa.storyapp.data.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
    ): AllStoriesResponse
//    @GET("stories")
//    suspend fun getStories(): AllStoriesResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

//UserModel
}