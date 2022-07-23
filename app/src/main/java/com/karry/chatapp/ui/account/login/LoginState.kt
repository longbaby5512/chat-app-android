package com.karry.chatapp.ui.account.login

import com.karry.chatapp.domain.model.Key
import com.karry.chatapp.domain.model.User

data class LoginState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val key: Key? = null,
    val token: String? = null,
    val error: String? = null
)