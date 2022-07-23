package com.karry.chatapp.data.dto.response


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.karry.chatapp.domain.model.User
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserResponse(
    @SerializedName("email") val email: String = "",
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("publicKey") val publicKey: String = ""
) : Parcelable

fun UserResponse.toUser() = User(
    email = email,
    id = id,
    name = name,
    publicKey = publicKey
)