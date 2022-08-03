package com.karry.chatapp.data.remote.api

import com.karry.chatapp.data.remote.dto.request.CreateUserRequest
import com.karry.chatapp.data.remote.dto.request.LoginRequest
import com.karry.chatapp.data.remote.dto.response.ConversationResponse
import com.karry.chatapp.data.remote.dto.response.LoginResponse
import com.karry.chatapp.data.remote.dto.response.MessageResponse
import retrofit2.http.*

interface AuthApi {
    @POST("v1/auth/register")
    suspend fun register(@Body inputs: CreateUserRequest)

    @POST("v1/auth/login")
    suspend fun login(@Body() inputs: LoginRequest): LoginResponse

    @GET("v1/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): LoginResponse

    @GET("v1/message/{id}")
    suspend fun getAllMessages(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): List<MessageResponse>

    @GET("v1/conversation")
    suspend fun getAllConversations(
        @Header("Authorization") token: String
    ): List<ConversationResponse>

    @GET("v1/auth/logout")
    suspend fun logout(@Header("Authorization") token: String)
}
