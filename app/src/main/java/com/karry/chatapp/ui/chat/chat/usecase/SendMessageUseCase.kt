package com.karry.chatapp.ui.chat.chat.usecase

import android.text.TextUtils
import com.google.gson.Gson
import com.karry.chaotic.Chaotic
import com.karry.chaotic.extentions.fromBase64
import com.karry.chaotic.extentions.toBase64
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.data.remote.api.ChatService
import com.karry.chatapp.data.remote.dto.request.MessageSent
import com.karry.chatapp.data.remote.dto.response.StatusResponse
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

class SendMessageUseCase @Inject constructor(
    private val chatService: ChatService,
    private val cypher: Chaotic,
    private val storage: Storage,
    private val gson: Gson
) {

    operator fun invoke(message: MessageSent): Flow<Resource<Message>> = flow {
        val suffix = listOf(message.to, ChatApplication.currentUser!!.id)
        val suffixSorted = suffix.sorted().joinToString("_")
        val keyStore = KEY_FINAL +"_"+suffixSorted
        val key = storage.get(keyStore, String::class.java)
        if (TextUtils.isEmpty(key)) {
            emit(Resource.Error("Key is empty"))
            return@flow
        }
        Timber.d("Final key: ${key.fromBase64().toHex()} at $keyStore")
        val content = message.content.toByteArray()

        Timber.d("Message sent before encrypt: ${message.content}")

        cypher.init(Cipher.ENCRYPT_MODE, key.fromBase64())
        val encryptMessage = cypher.doFinal(content)
        Timber.d("Message sent after encrypt: ${encryptMessage.toBase64()}")
        val messageSent = message.copy(content = encryptMessage.toBase64())
        val result = chatService.sendMessage(messageSent)
        val res = gson.fromJson(gson.toJson(result), StatusResponse::class.java)

        if (res.status) {
            emit(Resource.Success(res.message!!.copy(content = message.content).toMessage()))
        } else {
            emit(Resource.Error("Can't send message"))
        }
    }
}