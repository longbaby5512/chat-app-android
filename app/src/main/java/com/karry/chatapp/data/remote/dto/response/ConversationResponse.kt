package com.karry.chatapp.data.remote.dto.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Parcelize
data class ConversationResponse(
    @SerializedName("content") val content: String = "",
    @SerializedName("id") val id: Int = 0,
    @SerializedName("timestamp") val timestamp: String = "",
    @SerializedName("toUserId") val toUserId: Int = 0,
    @SerializedName("type") val type: String = "",
    @SerializedName("userId") val userId: Int = 0
) : Parcelable