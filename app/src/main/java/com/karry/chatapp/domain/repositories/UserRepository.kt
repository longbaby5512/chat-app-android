package com.karry.chatapp.domain.repositories

import com.karry.chatapp.data.dto.response.UserResponse

interface UserRepository {
    suspend fun getAllUsers(token: String): List<UserResponse>
    suspend fun getUserById(token: String, id: Int): UserResponse
    suspend fun getUserByEmail(token: String,email: String): UserResponse
}