package com.karry.chatapp.ui.chat.user_list.usecase

import com.karry.chatapp.data.dto.response.toUser
import com.karry.chatapp.domain.model.User
import com.karry.chatapp.domain.repositories.UserRepository
import com.karry.chatapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(token: String): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading)
        try {
            val bearerToken = "Bearer $token"
            val users = userRepository.getAllUsers(bearerToken)
            if (users.isNotEmpty()) {
                emit(Resource.Success(users.map { it.toUser() }))
            } else {
                emit(Resource.Empty)
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }
}