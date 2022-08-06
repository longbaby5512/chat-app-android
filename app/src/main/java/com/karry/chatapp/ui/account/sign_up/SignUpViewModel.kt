package com.karry.chatapp.ui.account.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karry.chaotic.Chaotic
import com.karry.chaotic.ECDH
import com.karry.chaotic.extentions.toBase64
import com.karry.chatapp.data.remote.dto.request.CreateUserRequest
import com.karry.chatapp.domain.model.Key
import com.karry.chatapp.ui.account.sign_up.usecase.SignUpUseCase
import com.karry.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.crypto.Cipher
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val cypher: Chaotic
) : ViewModel() {
    private var _state = MutableStateFlow(SignUpState())
    val state get() = _state.asStateFlow()

    fun signUp(name: String, email: String, password: String) {
        val ecdh = ECDH()

        cypher.init(Cipher.ENCRYPT_MODE, password.toByteArray())

        val publicKey = ecdh.publicKey.toBase64()
        val privateKey = cypher.doFinal(ecdh.privateKey).toBase64()

        val key = Key(
            privateKey = privateKey,
            publicKey = publicKey
        )
        val inputs = CreateUserRequest(name, email, password, key)
        signUpUseCase(inputs).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value =
                            state.value.copy(isLoading = false, isSuccess = true, error = null)
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.exception
                    )
                }
                else -> {
                    _state.value = state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}