package com.karry.chatapp.data.dto.response


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.karry.chatapp.domain.model.Key
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class KeyResponse(
    @SerializedName("privateKey") val privateKey: String = "",
    @SerializedName("publicKey") val publicKey: String = ""
) : Parcelable

fun KeyResponse.toKey(): Key {
    return Key(publicKey, privateKey)
}