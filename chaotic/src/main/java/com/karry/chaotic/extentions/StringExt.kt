package com.karry.chaotic.extentions

import android.util.Base64

fun String.fromBase64Url(): ByteArray {
    return Base64.decode(this, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}