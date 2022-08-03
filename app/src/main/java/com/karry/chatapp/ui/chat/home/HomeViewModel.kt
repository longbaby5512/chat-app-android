package com.karry.chatapp.ui.chat.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.ui.chat.home.usecase.GetAllConversationsUseCase
import com.karry.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val getAllConversationsUseCase: GetAllConversationsUseCase) :
    ViewModel() {
    private var _state = MutableStateFlow(HomeState())
    val state get() = _state.asStateFlow()

    init {
        getAllConversations(ChatApplication.accessToken!!)
    }

    private fun getAllConversations(token: String) {
        getAllConversationsUseCase(token).onEach { result ->
            // Type of result
            Timber.d("result: $result")
            when (result) {
                is Resource.Empty -> {
                    _state.value = state.value.copy(isLoading = false, isEmpty = true)
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(isLoading = false, error = result.exception)
                }
                is Resource.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        isEmpty = false,
                        conversations = result.data
                    )
                }
            }

        }.launchIn(viewModelScope)
    }
}