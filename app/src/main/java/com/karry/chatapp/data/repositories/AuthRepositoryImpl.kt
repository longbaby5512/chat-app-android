package com.karry.chatapp.data.repositories

import com.karry.chatapp.data.api.AuthApi
import com.karry.chatapp.data.dto.request.CreateUserRequest
import com.karry.chatapp.data.dto.request.LoginRequest
import com.karry.chatapp.data.dto.response.LoginResponse
import com.karry.chatapp.data.dto.response.MessageResponse
import com.karry.chatapp.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi) : AuthRepository {
    override suspend fun register(inputs: CreateUserRequest) {
        return authApi.register(inputs)
    }

    override suspend fun login(inputs: LoginRequest): LoginResponse {
        return authApi.login(inputs)
    }

    override suspend fun getProfile(token: String): LoginResponse {
        return authApi.getProfile(token)
    }

    override suspend fun getAllMessages(token: String, id: Int): List<MessageResponse> {
        return authApi.getAllMessages(token, id)
    }

    override suspend fun getFinalKey(token: String, theirPublicKey: String): String {
        return authApi.getFinalKey(token, theirPublicKey)
    }
}