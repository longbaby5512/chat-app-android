package com.karry.chatapp.data.api

import com.karry.chatapp.data.dto.request.CreateUserRequest
import com.karry.chatapp.data.dto.request.LoginRequest
import com.karry.chatapp.data.dto.response.LoginResponse
import com.karry.chatapp.data.dto.response.MessageResponse
import retrofit2.http.*

interface AuthApi {
    @POST("register")
    suspend fun register(@Body inputs: CreateUserRequest)

    @POST("login")
    suspend fun login(@Body() inputs: LoginRequest): LoginResponse

    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): LoginResponse

    @GET("message")
    suspend fun getAllMessages(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): List<MessageResponse>

    @GET("finalkey")
    suspend fun getFinalKey(
        @Header("Authorization") token: String,
        @Query("theirPublicKey") theirPublicKey: String
    ): String
}
