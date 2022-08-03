package com.karry.chatapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation(
    val user: User,
    val content: String,
    val timestamp: String,
    val type: MessageType
): Parcelable