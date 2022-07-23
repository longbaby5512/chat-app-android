package com.karry.chatapp.ui.chat.chat

import com.karry.chatapp.domain.model.Message

data class ChatState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val messages: List<Message> = listOf(),
    val error: String? = null,
    val hasNewMessages: Boolean = false,
)