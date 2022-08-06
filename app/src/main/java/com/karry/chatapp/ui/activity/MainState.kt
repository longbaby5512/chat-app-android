package com.karry.chatapp.ui.activity

data class MainState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)