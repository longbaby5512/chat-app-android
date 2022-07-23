package com.karry.chatapp.ui.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chaotic.Chaotic
import com.karry.chaotic.ECDH
import com.karry.chaotic.extentions.fromBase64Url
import com.karry.chaotic.extentions.toBase64Url
import com.karry.chatapp.data.dto.request.LoginRequest
import com.karry.chatapp.data.dto.response.toKey
import com.karry.chatapp.data.dto.response.toUser
import com.karry.chatapp.domain.model.Key
import com.karry.chatapp.ui.account.login.usecase.LoginUseCase
import com.karry.chatapp.utils.Resource
import com.karry.chatapp.utils.extentions.toHex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
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
        val ecdh = ECDH(password)

        cypher.init(Cipher.ENCRYPT_MODE, password.toByteArray())

        val publicKey = ecdh.publicKey.toBase64Url()
        val privateKey = cypher.doFinal(ecdh.privateKey).toBase64Url()

        val key = Key(
            privateKey = privateKey,
            publicKey = publicKey
        )


        val loginRequest = LoginRequest(email, password, key)
        loginUseCase(loginRequest).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val privateKeyResponse = result.data.key.privateKey.fromBase64Url()
                    Timber.d(
                        "After: Private: ${privateKeyResponse.toHex()}, Public: ${
                            result.data.key.publicKey.fromBase64Url().toHex()
                        }"
                    )
                    cypher.init(Cipher.DECRYPT_MODE, password.toByteArray())

                    val decryptedPrivateKeyResponse = cypher.doFinal(privateKeyResponse)
                    Timber.d(
                        "After: Private: ${decryptedPrivateKeyResponse.toHex()}, Public: ${
                            result.data.key.publicKey.fromBase64Url().toHex()
                        }"
                    )
                    _state.value = state.value.copy(
                        isLoading = false,
                        user = result.data.toUser(),
                        key = result.data.key.copy(privateKey = decryptedPrivateKeyResponse.toBase64Url())
                            .toKey(),
                        token = result.data.token
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