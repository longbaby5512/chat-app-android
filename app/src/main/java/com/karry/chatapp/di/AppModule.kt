package com.karry.chatapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.karry.chaotic.Chaotic
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideChaoticCypher(): Chaotic {
        return Chaotic.getInstance()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }
}