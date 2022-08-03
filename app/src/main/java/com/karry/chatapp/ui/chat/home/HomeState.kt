package com.karry.chatapp.ui.chat.home

import com.karry.chatapp.domain.model.Conversation

data class HomeState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val conversations: List<Conversation> = listOf(),
    val error: String? = null,
)