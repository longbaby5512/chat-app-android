package com.karry.chatapp.ui.chat.user_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chatapp.ui.chat.user_list.usecase.GetAllUsersUseCase
import com.karry.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(private val getAllUsersUseCase: GetAllUsersUseCase): ViewModel() {
private var _state = MutableStateFlow(UserListState())
    val state = _state.asStateFlow()

    fun getAllUsers(token: String) {
        getAllUsersUseCase(token).onEach {  result ->
            when(result) {
                is Resource.Loading -> {
                    _state.value = state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    _state.value = state.value.copy(isLoading = false, users = result.data, isEmpty = false)
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(isLoading = false, error = result.exception, isEmpty = true)
                }
                is Resource.Empty -> {
                    _state.value = state.value.copy(isLoading = false, isEmpty = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}