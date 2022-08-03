package com.karry.chatapp.ui.chat.home.usecase

import android.text.TextUtils
import com.karry.chaotic.Chaotic
import com.karry.chaotic.ECDH
import com.karry.chaotic.extentions.fromBase64
import com.karry.chaotic.extentions.toBase64
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.data.remote.dto.response.toUser
import com.karry.chatapp.domain.model.Conversation
import com.karry.chatapp.domain.model.toMessageType
import com.karry.chatapp.domain.repositories.AuthRepository
import com.karry.chatapp.domain.repositories.UserRepository
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

class GetAllConversationsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chaotic: Chaotic,
    private val storage: Storage
) {
    operator fun invoke(token: String): Flow<Resource<List<Conversation>>> = flow {
        emit(Resource.Loading)
        try {
            val bearerToken = "Bearer $token"
            val response = authRepository.getAllConversations(bearerToken)
            Timber.d("response: $response")
            if (response.isEmpty()) {
                emit(Resource.Empty)
                return@flow
            }
            val conversations = response.map {
                val id = if(it.userId == ChatApplication.currentUser!!.id) it.toUserId else it.userId
                val user = userRepository.getUserById(bearerToken, id).toUser()

                val suffix = listOf(id, ChatApplication.currentUser!!.id)
                val suffixSorted = suffix.sorted().joinToString("_")
                val keyStore = KEY_FINAL + "_" + suffixSorted

                var key = storage.get(keyStore, String::class.java)
                if (TextUtils.isEmpty(key)) {
                    val shareSecret =
                            ECDH.generateSecretKey(ChatApplication.key!!.privateKey!!, user.publicKey!!)
                                .toBase64()
                    Timber.d("Share Secret: ${shareSecret.fromBase64().toHex()}")
                    key = ECDH.generateFinalKey(
                        ChatApplication.key!!.publicKey,
                        user.publicKey,
                        shareSecret
                    ).toBase64()
                }

                if (TextUtils.isEmpty(key)) {
                    emit(Resource.Error("Key is empty"))
                    return@flow
                }
                storage.set(keyStore, key)
                chaotic.init(Cipher.DECRYPT_MODE, key.fromBase64())
                val decryptedContent = chaotic.doFinal(it.content.fromBase64())

                Conversation(
                    user =  user,
                    content = String(decryptedContent),
                    timestamp = it.timestamp,
                    type = it.type.toMessageType(),
                )
            }
            emit(Resource.Success(conversations))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Internet connection error"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }
}