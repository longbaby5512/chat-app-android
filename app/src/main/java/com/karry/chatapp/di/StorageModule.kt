package com.karry.chatapp.di

import com.karry.chatapp.utils.storage.SecurePreferencesStorage
import com.karry.chatapp.utils.storage.Storage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    @Singleton
    abstract fun bindStorage(storage: SecurePreferencesStorage): Storage
}