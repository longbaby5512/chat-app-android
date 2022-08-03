package com.karry.chatapp.utils

import com.karry.chatapp.BuildConfig

const val CHANNEL_ID = "karry"

// flag to identify whether to show single line
// or multi line text in push notification tray
var appendNotificationMessages = true
const val TOPIC_GLOBAL = "global"
const val SEND_FCM_TOKEN_TO_SERVER = "send_fcm_token_to_server"
const val PUSH_NOTIFICATION = "push_notification"

const val PUSH_TYPE_CHATROOM = 1
const val PUSH_TYPE_USER = 2

const val NOTIFICATION_ID = 100
const val NOTIFICATION_ID_BIG_IMAGE = 101

const val BASE_URL: String = BuildConfig.BASE_URL

const val SEND_MESSAGE = "send_message"
const val RECEIVE_MESSAGE = "receive_message"

const val SHARED_PREF_NAME = "secret_shared_prefs"
const val KEY_ACCESS_TOKEN = "access_token"
const val KEY_REFRESH_TOKEN = "refresh_token"
const val KEY_CURRENT_USER = "current_user"
const val KEY_SECRET_CRYPTO = "secret_crypto"
const val KEY_NOTIFICATIONS = "notifications"
const val KEY_DEVICE_TOKEN = "device_token"
const val KEY_FINAL = "final"

const val ONESIGNAL_APP_ID = "f1d68b59-ae50-4dc4-b9b7-38525de10dac"

