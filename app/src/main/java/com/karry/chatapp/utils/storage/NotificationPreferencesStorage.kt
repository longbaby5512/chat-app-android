package com.karry.chatapp.utils.storage

import com.karry.chatapp.utils.KEY_NOTIFICATIONS
import javax.inject.Inject

class NotificationPreferencesStorage @Inject constructor(private val storage: Storage) {

    val notifications: String get() = storage.get(KEY_NOTIFICATIONS, String::class.java)
    fun addNotification(newNotification: String) {
        var oldNotification = notifications
        if (oldNotification.isEmpty()) {
            oldNotification += "|$newNotification"
        } else {
            oldNotification = newNotification
        }
        storage.set(KEY_NOTIFICATIONS, oldNotification)
    }
}