package com.karry.chatapp.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chatapp.ui.activity.usecase.LogoutUseCase
import com.karry.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val logoutUseCase: LogoutUseCase): ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun logout(token: String) {
        logoutUseCase(token).onEach {
            when (it) {
                is Resource.Success -> _state.value = state.value.copy(isLoading = false, isSuccess = true)
                is Resource.Error -> _state.value = state.value.copy(isLoading = false, error = it.exception)
                else -> _state.value = state.value.copy(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }
}