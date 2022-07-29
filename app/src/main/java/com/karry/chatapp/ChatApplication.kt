package com.karry.chatapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.karry.chatapp.domain.model.Key
import com.karry.chatapp.domain.model.User
import com.karry.chatapp.utils.KEY_CURRENT_USER
import com.karry.chatapp.utils.KEY_SECRET_CRYPTO
import com.karry.chatapp.utils.KEY_ACCESS_TOKEN
import com.karry.chatapp.utils.ONESIGNAL_APP_ID
import com.karry.chatapp.utils.storage.Storage
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ChatApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var storage: Storage


    override fun onCreate() {
        super.onCreate()

        currentUser = storage.get(KEY_CURRENT_USER, User::class.java)
        accessToken = storage.get(KEY_ACCESS_TOKEN, String::class.java)
        key = storage.get(KEY_SECRET_CRYPTO, Key::class.java)

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        if (BuildConfig.DEBUG) {
            // Strict Mode

            Timber.plant(Timber.DebugTree())
            Timber.d("Debug mode")
            Timber.d("User: $currentUser")
            Timber.d("Access Token: $accessToken")
            Timber.d("Key: $key")
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    companion object {
        var currentUser: User ?=null
        var accessToken: String ?=null
        var refreshToken: String ?=null
        var key: Key?=null

        @JvmStatic
        fun clear() {
            currentUser = null
            accessToken = null
            refreshToken = null
            key = null
        }
    }
}