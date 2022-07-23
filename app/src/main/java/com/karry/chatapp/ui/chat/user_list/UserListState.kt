package com.karry.chatapp.ui.chat.user_list

import com.karry.chatapp.domain.model.User

data class UserListState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val users: List<User> = listOf(),
    val error: String? = null,
)