package com.karry.chatapp.data.repositories

import com.karry.chatapp.data.api.UserApi
import com.karry.chatapp.data.dto.response.UserResponse
import com.karry.chatapp.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userApi: UserApi) : UserRepository {
    override suspend fun getAllUsers(token: String): List<UserResponse> {
        return userApi.getAllUsers(token)
    }

    override suspend fun getUserById(token: String, id: Int): UserResponse {
        return userApi.getUserById(token, id)
    }

    override suspend fun getUserByEmail(token: String, email: String): UserResponse {
        return userApi.getUserByEmail(token, email)
    }
}