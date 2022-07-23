package com.karry.chatapp.ui.account.sign_up.usecase

import com.karry.chatapp.data.dto.request.CreateUserRequest
import com.karry.chatapp.domain.repositories.AuthRepository
import com.karry.chatapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(inputs: CreateUserRequest): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        try {
            authRepository.register(inputs)
            emit(Resource.Success(true))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Error"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error"))
        }
    }
}