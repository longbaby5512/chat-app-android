package com.karry.chatapp.domain.model

import org.json.JSONObject

data class Message(
    val from: Int,
    val to: Int,
    val content: String,
    val time: String,
    val type: MessageType = MessageType.TEXT
)

enum class MessageType constructor(val value: String) {
    TEXT("text"),
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    FILE("file"),
    LOCATION("location"),
    CONTACT("contact"),
    UNKNOWN("unknown")
}

fun String.toMessageType(): MessageType {
    return when (this) {
        "text" -> MessageType.TEXT
        "image" -> MessageType.IMAGE
        "video" -> MessageType.VIDEO
        "audio" -> MessageType.AUDIO
        "file" -> MessageType.FILE
        "location" -> MessageType.LOCATION
        "contact" -> MessageType.CONTACT
        else -> MessageType.UNKNOWN
    }
}

fun MessageType.toTypeString(): String {
    return when (this) {
        MessageType.TEXT -> "text"
        MessageType.IMAGE -> "image"
        MessageType.VIDEO -> "video"
        MessageType.AUDIO -> "audio"
        MessageType.FILE -> "file"
        MessageType.LOCATION -> "location"
        MessageType.CONTACT -> "contact"
        MessageType.UNKNOWN -> "unknown"
    }
}