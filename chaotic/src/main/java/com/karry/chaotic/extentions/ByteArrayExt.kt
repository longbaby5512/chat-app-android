package com.karry.chaotic.extentions

import android.util.Base64

fun ByteArray.toBase64Url(): String = Base64.encodeToString(this, Base64.NO_PADDING or Base64.URL_SAFE)