package com.karry.chatapp.data.remote.api

import com.karry.chatapp.data.remote.dto.request.MessageSent
import com.karry.chatapp.data.remote.dto.response.MessageResponse
import com.karry.chatapp.socketio.annotation.*
import com.karry.chatapp.utils.RECEIVE_MESSAGE
import com.karry.chatapp.utils.SEND_MESSAGE
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow


interface ChatService {
    @Connect
    suspend fun connect()

    @Disconnect
    suspend fun disconnect()

    @Emit(SEND_MESSAGE)
    suspend fun sendMessage(message: MessageSent): Any

    @On(RECEIVE_MESSAGE)
    fun onMessageReceived(): Flow<MessageResponse>

    @Off
    fun off()
}