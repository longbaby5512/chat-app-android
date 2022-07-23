package com.karry.chatapp.ui.chat.chat.usecase

import android.text.TextUtils
import com.karry.chaotic.Chaotic
import com.karry.chaotic.ECDH
import com.karry.chaotic.extentions.fromBase64Url
import com.karry.chaotic.extentions.toBase64Url
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.data.dto.response.toMessage
import com.karry.chatapp.domain.model.Message
import com.karry.chatapp.domain.repositories.AuthRepository
import com.karry.chatapp.utils.KEY_FINAL
import com.karry.chatapp.utils.Resource
import com.karry.chatapp.utils.extentions.toHex
import com.karry.chatapp.utils.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.crypto.Cipher
import javax.inject.Inject

class GetAllMessagesUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val chaotic: Chaotic,
    private val storage: Storage
) {
    operator fun invoke(token: String, id: Int, publicKey: String): Flow<Resource<List<Message>>> =
        flow {
            emit(Resource.Loading)
            try {
                val bearerToken = "Bearer $token"

                val suffix = listOf(id, ChatApplication.currentUser!!.id)
                val suffixSorted = suffix.sorted().joinToString("_")
                val keyStore = KEY_FINAL + "_" + suffixSorted
                var key = storage.get(keyStore, String::class.java)
                Timber.d(
                    "Our Private: ${
                        ChatApplication.key!!.privateKey!!.fromBase64Url().toHex()
                    }, Their Public: ${publicKey.fromBase64Url().toHex()}"
                )
                if (TextUtils.isEmpty(key)) {
                    val shareSecret =
                        ECDH.generateSecretKey(ChatApplication.key!!.privateKey!!, publicKey)
                            .toBase64Url()
                    Timber.d("Share Secret: ${shareSecret.fromBase64Url().toHex()}")
                    key = ECDH.generateFinalKey(
                        ChatApplication.key!!.publicKey,
                        publicKey,
                        shareSecret
                    ).toBase64Url()
                }

                if (TextUtils.isEmpty(key)) {
                    emit(Resource.Error("Key is empty"))
                    return@flow
                }

                Timber.e("Final key: ${key.fromBase64Url().toHex()} at keyStore: $keyStore")
                storage.set(keyStore, key)
                val response = authRepository.getAllMessages(bearerToken, id)
                if (response.isNotEmpty()) {
                    emit(Resource.Success(response.map {
                        val content = it.content
                        chaotic.init(Cipher.DECRYPT_MODE, key.fromBase64Url())
                        val decryptedContent = chaotic.doFinal(content.fromBase64Url())
                        Timber.e("cypherContent $content decryptedContent: $decryptedContent")
                        return@map it.copy(content = String(decryptedContent))
                            .toMessage()
                    }))
                } else {
                    emit(Resource.Empty)
                }
            } catch (e: HttpException) {
                Timber.e(e)
                emit(Resource.Error(e.message()))
            } catch (e: IOException) {
                Timber.e(e)
                emit(Resource.Error(e.message ?: "Error Occurred!"))
            } catch (e: Exception) {
                Timber.e(e)
                emit(Resource.Error(e.message ?: "Error Occurred!"))
            }
        }
}