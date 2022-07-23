package com.karry.chatapp.data.dto.response


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.karry.chatapp.domain.model.User
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LoginResponse(
    @SerializedName("email") val email: String = "",
    @SerializedName("id") val id: Int = 0,
    @SerializedName("key") val key: KeyResponse = KeyResponse(),
    @SerializedName("name") val name: String = "",
    @SerializedName("token") val token: String = ""
) : Parcelable

fun LoginResponse.toUser() = User(
    email = email,
    id = id,
    name = name
)