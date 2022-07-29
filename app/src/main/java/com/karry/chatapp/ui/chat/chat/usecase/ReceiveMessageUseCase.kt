package com.karry.chatapp.ui.chat.chat.usecase

import android.text.TextUtils
import com.karry.chaotic.Chaotic
import com.karry.chaotic.extentions.fromBase64
import com.karry.chatapp.data.remote.api.ChatService
import com.karry.chatapp.data.remote.dto.response.toMessage
import com.karry.chatapp.domain.model.Message
import com.karry.chatapp.utils.KEY_FINAL
import com.karry.chatapp.utils.Resource
import com.karry.chatapp.utils.extentions.toHex
import com.karry.chatapp.utils.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.crypto.Cipher
import javax.inject.Inject

class ReceiveMessageUseCase @Inject constructor(
    private val chatService: ChatService,
    private val cypher: Chaotic,
    private val storage: Storage
) {
    operator fun invoke(): Flow<Resource<Message>> = flow {
        chatService.onMessageReceived().collect {
            val suffix = listOf(it.from, it.to)
            val suffixSorted = suffix.sorted().joinToString("_")
            val keyStore = KEY_FINAL + "_" + suffixSorted
            val key = storage.get(keyStore, String::class.java)
            if (TextUtils.isEmpty(key)) {
                emit(Resource.Error("Key is empty"))
                return@collect
            }
            Timber.d("Final key: ${key.fromBase64().toHex()} at $keyStore")
            Timber.d("Message received before decrypt: ${it.content}")
            cypher.init(Cipher.DECRYPT_MODE, key.fromBase64())
            val decryptedMessage = cypher.doFinal(it.content.fromBase64())
            val message = it.copy(content = String(decryptedMessage)).toMessage()
            Timber.d("Message received after decrypt: ${message.content}")
            emit(Resource.Success(message))
        }
    }
}