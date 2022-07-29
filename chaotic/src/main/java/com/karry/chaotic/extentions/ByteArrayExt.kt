package com.karry.chaotic.extentions

import android.util.Base64

fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.DEFAULT)