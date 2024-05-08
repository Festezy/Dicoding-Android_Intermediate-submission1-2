package com.ariqa.storyapp.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("error")
    val error: Boolean
)
