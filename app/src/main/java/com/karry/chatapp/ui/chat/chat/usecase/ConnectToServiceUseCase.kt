package com.karry.chatapp.ui.chat.chat.usecase

import com.karry.chatapp.data.remote.api.ChatService
import timber.log.Timber
import javax.inject.Inject

class ConnectToServiceUseCase @Inject constructor(private val chatService: ChatService) {
    suspend operator fun invoke() {
        Timber.d("Connecting to service")
        chatService.connect()
    }
}