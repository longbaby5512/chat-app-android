package com.karry.chatapp.data.remote.api

import com.karry.chatapp.data.remote.dto.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("v1/user")
    suspend fun getAllUsers(@Header("Authorization") token: String): List<UserResponse>

    @GET("v1/user/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): UserResponse

    @GET("v1/user/email/{email}")
    suspend fun getUserByEmail(
        @Header("Authorization") token: String,
        @Query("email") email: String
    ): UserResponse

}