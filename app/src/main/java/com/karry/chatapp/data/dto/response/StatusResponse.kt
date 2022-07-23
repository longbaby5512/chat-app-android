package com.karry.chatapp.data.dto.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class StatusResponse(
    val status: Boolean = false,
    val message: MessageResponse? = null
) : Parcelable