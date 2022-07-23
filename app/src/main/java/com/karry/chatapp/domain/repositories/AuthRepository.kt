package com.karry.chatapp.domain.repositories

import com.karry.chatapp.data.dto.request.CreateUserRequest
import com.karry.chatapp.data.dto.request.LoginRequest
import com.karry.chatapp.data.dto.response.LoginResponse
import com.karry.chatapp.data.dto.response.MessageResponse

interface AuthRepository {
    suspend fun register(inputs: CreateUserRequest)
    suspend fun login(inputs: LoginRequest): LoginResponse
    suspend fun getProfile(token: String): LoginResponse
    suspend fun getAllMessages(token: String, id: Int): List<MessageResponse>
    suspend fun getFinalKey(token: String, theirPublicKey: String): String
}