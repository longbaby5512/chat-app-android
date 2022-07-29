package com.karry.chaotic.extentions

import android.util.Base64

fun String.fromBase64(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}