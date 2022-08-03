package com.karry.chatapp.domain.repositories

import com.karry.chatapp.data.remote.dto.request.CreateUserRequest
import com.karry.chatapp.data.remote.dto.request.LoginRequest
import com.karry.chatapp.data.remote.dto.response.ConversationResponse
import com.karry.chatapp.data.remote.dto.response.LoginResponse
import com.karry.chatapp.data.remote.dto.response.MessageResponse

interface AuthRepository {
    suspend fun register(inputs: CreateUserRequest)
    suspend fun login(inputs: LoginRequest): LoginResponse
    suspend fun getProfile(token: String): LoginResponse
    suspend fun getAllMessages(token: String, id: Int): List<MessageResponse>
    suspend fun getAllConversations(token: String): List<ConversationResponse>
    suspend fun logout(token: String)
}