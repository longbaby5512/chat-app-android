package com.karry.chatapp.ui.activity.usecase

import com.karry.chatapp.domain.repositories.AuthRepository
import com.karry.chatapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(token: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val bearerToken = "Bearer $token"
            authRepository.logout(bearerToken)
            emit(Resource.Success(Unit))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }
}