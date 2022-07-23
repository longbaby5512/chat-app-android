package com.karry.chatapp.ui.account.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chatapp.data.dto.request.CreateUserRequest
import com.karry.chatapp.ui.account.sign_up.usecase.SignUpUseCase
import com.karry.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val signUpUseCase: SignUpUseCase) : ViewModel() {
    private var _state = MutableStateFlow(SignUpState())
    val state get() = _state.asStateFlow()

    fun signUp(name: String, email: String, password: String) {
        val inputs = CreateUserRequest(name, email, password)
        signUpUseCase(inputs).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(isLoading = false, isSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(isLoading = false, error = result.exception)
                }
                else -> {
                    _state.value = state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}