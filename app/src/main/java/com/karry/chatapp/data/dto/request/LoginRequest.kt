package com.karry.chatapp.data.dto.request

import com.karry.chatapp.domain.model.Key

data class LoginRequest(
    val email: String,
    val password: String,
    val key: Key
)