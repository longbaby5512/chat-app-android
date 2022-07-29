package com.karry.chatapp.ui.chat.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chatapp.data.remote.dto.request.MessageSent
import com.karry.chatapp.domain.model.MessageType
import com.karry.chatapp.domain.model.toTypeString
import com.karry.chatapp.ui.chat.chat.usecase.*
import com.karry.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getAllMessagesUseCase: GetAllMessagesUseCase,
    private val disconnectSocketUseCase: DisconnectSocketUseCase,
    private val connectToServiceUseCase: ConnectToServiceUseCase,
    private val receiveMessageUseCase: ReceiveMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {
    private var _state = MutableStateFlow(ChatState())
    val state get() = _state.asStateFlow()


    fun getAllMessages(token: String, id: Int, key: String) {
        getAllMessagesUseCase(token, id, key).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
                }
                is Resource.Empty -> {
                    _state.value = state.value.copy(isLoading = false, isEmpty = true)
                }
                is Resource.Success -> {
                    Timber.d("Success")
                    _state.value = state.value.copy(
                        isLoading = false,
                        isEmpty = false,
                        messages = result.data,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        isEmpty = false,
                        error = result.exception
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun connectSocket() {
        viewModelScope.launch {
            connectToServiceUseCase()
            receiveMessageUseCase().collect {
                if (it is Resource.Success) {
                    Timber.d("Success")
                    val messages = state.value.messages.toMutableList()
                    messages.add(it.data)
                    _state.value = state.value.copy(
                        messages = messages,
                        isEmpty = false,
                        error = null,
                        isLoading = false
                    )
                }

                if (it is Resource.Error) {
                    Timber.d("Error")
                    _state.value = state.value.copy(error = it.exception, isLoading = false)
                }
            }
        }
    }

    fun sendMessage(content: String, to: Int, type: MessageType) {
        val message = MessageSent(to, content, type.toTypeString())
        sendMessageUseCase(message).onEach {
            if (it is Resource.Success) {
                val messages = state.value.messages.toMutableList()
                messages.add(it.data)
                _state.value = state.value.copy(
                    messages = messages,
                    error = null,
                    isLoading = false,
                    isEmpty = false
                )
            }
            if (it is Resource.Error) {
                _state.value = state.value.copy(error = it.exception)
            }
        }.launchIn(viewModelScope)
    }

    fun disconnectSocket() = viewModelScope.launch {
        disconnectSocketUseCase()
    }
}