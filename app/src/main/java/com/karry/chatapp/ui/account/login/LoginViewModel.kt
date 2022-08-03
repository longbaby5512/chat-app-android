package com.karry.chatapp.ui.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chaotic.Chaotic
import com.karry.chaotic.ECDH
import com.karry.chaotic.Hash
import com.karry.chaotic.extentions.fromBase64
import com.karry.chaotic.extentions.toBase64
import com.karry.chatapp.data.remote.dto.request.LoginRequest
import com.karry.chatapp.data.remote.dto.response.toKey
import com.karry.chatapp.data.remote.dto.response.toUser
import com.karry.chatapp.domain.model.Key
import com.karry.chatapp.ui.account.login.usecase.LoginUseCase
import com.karry.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.crypto.Cipher
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val cypher: Chaotic
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state get() = _state.asStateFlow()

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        loginUseCase(loginRequest).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val privateKeyResponse = result.data.key.privateKey.fromBase64()
                    cypher.init(Cipher.DECRYPT_MODE, password.toByteArray())

                    val decryptedPrivateKeyResponse = cypher.doFinal(privateKeyResponse)
                    _state.value = state.value.copy(
                        isLoading = false,
                        user = result.data.toUser(),
                        key = result.data.key.copy(privateKey = decryptedPrivateKeyResponse.toBase64())
                            .toKey(),
                        token = result.data.token,
                        refreshToken = result.data.refreshToken
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        error = result.exception
                    )
                }
                else -> {
                    _state.value = state.value.copy(
                        isLoading = true
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}