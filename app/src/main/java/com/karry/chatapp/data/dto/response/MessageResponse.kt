package com.karry.chatapp.data.dto.response


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.karry.chatapp.domain.model.Message
import com.karry.chatapp.domain.model.toMessageType
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MessageResponse(
    @SerializedName("content") val content: String = "",
    @SerializedName("from") val from: Int = 0,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("timestamp") val timestamp: String = "",
    @SerializedName("to") val to: Int = 0,
    @SerializedName("type") val type: String = ""
) : Parcelable

fun MessageResponse.toMessage(): Message {
    return Message(
        content = content,
        from = from,
        time = timestamp,
        to = to,
        type = type.toMessageType()
    )
}