package com.karry.chatapp.data.remote.api

import com.karry.chatapp.data.remote.dto.request.CreateUserRequest
import com.karry.chatapp.data.remote.dto.request.LoginRequest
import com.karry.chatapp.data.remote.dto.response.LoginResponse
import com.karry.chatapp.data.remote.dto.response.MessageResponse
import retrofit2.http.*

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body inputs: CreateUserRequest)

    @POST("auth/login")
    suspend fun login(@Body() inputs: LoginRequest): LoginResponse

    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): LoginResponse

    @GET("message/{id}")
    suspend fun getAllMessages(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): List<MessageResponse>

    @GET("auth/logout")
    suspend fun logout(@Header("Authorization") token: String)
}
