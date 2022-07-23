package com.karry.chatapp.ui.account.login.usecase

import com.karry.chatapp.data.dto.request.LoginRequest
import com.karry.chatapp.data.dto.response.LoginResponse
import com.karry.chatapp.domain.repositories.AuthRepository
import com.karry.chatapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(inputs: LoginRequest): Flow<Resource<LoginResponse>> = flow {
        try {
            emit(Resource.Loading)
            val response = authRepository.login(inputs)
            Timber.d("Login response: $response")
            emit(Resource.Success(response))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message()))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error("Something went wrong. Please try again later."))
        }
    }
}