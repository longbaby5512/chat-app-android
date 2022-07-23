package com.karry.chatapp.utils.extentions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.Base64

fun ByteArray.toBitmap(): Bitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)
    ByteArrayOutputStream().apply {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
        return bitmap
    }
}

fun ByteArray.toBitmap(quality: Int): Bitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)
    ByteArrayOutputStream().apply {
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, this)
        return bitmap
    }
}

fun ByteArray.toHex(): String =
    joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

fun ByteArray.toBase64String(): String {
    return Base64.getEncoder().encodeToString(this)
}

fun ByteArray.toBase64ByteArray(): ByteArray {
    return Base64.getEncoder().encode(this)
}

fun ByteArray.fromBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}