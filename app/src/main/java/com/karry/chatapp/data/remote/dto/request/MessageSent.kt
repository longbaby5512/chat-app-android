package com.karry.chatapp.data.remote.dto.request

data class MessageSent(
    val to: Int,
    val content: String,
    val type: String,
)
