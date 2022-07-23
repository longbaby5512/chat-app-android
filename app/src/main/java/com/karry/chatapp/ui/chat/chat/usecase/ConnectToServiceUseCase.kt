package com.karry.chatapp.ui.chat.chat.usecase

import com.karry.chatapp.data.api.ChatService
import javax.inject.Inject

class ConnectToServiceUseCase @Inject constructor(private val chatService: ChatService) {
    suspend operator fun invoke() = chatService.connect()
}