package com.karry.chatapp.data.remote.dto.request

import com.karry.chatapp.domain.model.Key

data class LoginRequest(
    val email: String,
    val password: String,
    val key: Key
)