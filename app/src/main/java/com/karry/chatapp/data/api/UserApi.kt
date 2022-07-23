package com.karry.chatapp.data.api

import com.karry.chatapp.data.dto.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UserApi {
    @GET("user")
    suspend fun getAllUsers(@Header("Authorization") token: String): List<UserResponse>

    @GET("user")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): UserResponse

    @GET("user")
    suspend fun getUserByEmail(
        @Header("Authorization") token: String,
        @Query("email") email: String
    ): UserResponse

}