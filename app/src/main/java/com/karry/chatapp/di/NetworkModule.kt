package com.karry.chatapp.di

import com.google.gson.Gson
import com.karry.chatapp.BuildConfig
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.data.api.AuthApi
import com.karry.chatapp.data.api.ChatService
import com.karry.chatapp.data.api.UserApi
import com.karry.chatapp.data.repositories.AuthRepositoryImpl
import com.karry.chatapp.data.repositories.UserRepositoryImpl
import com.karry.chatapp.domain.repositories.AuthRepository
import com.karry.chatapp.domain.repositories.UserRepository
import com.karry.chatapp.utils.BASE_URL
import com.semicolon.cocket.CocketClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    companion object {
        @Provides
        @Singleton
        fun provideHttpLoggingInterceptor(): Interceptor {
            return HttpLoggingInterceptor { message ->
                Timber.tag("HttpLogging").i(message)
            }.apply {
                level = if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else
                    HttpLoggingInterceptor.Level.BASIC
            }
        }

        @Provides
        @Singleton
        internal fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }


        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }

        @Provides
        @Singleton
        fun provideUserApi(retrofit: Retrofit): UserApi {
            return retrofit.create(UserApi::class.java)
        }

        @Provides
        @Singleton
        fun provideSocket(): CocketClient {
            val options = IO.Options.builder()
                .setQuery("token=${ChatApplication.accessToken!!}")
                .build()
            return CocketClient.Builder().baseUrl(BASE_URL).options(options).build()
        }

        @Provides
        @Singleton
        fun provideChatSocket(socket: CocketClient): ChatService {
            return socket.create(ChatService::class.java)
        }
    }
}