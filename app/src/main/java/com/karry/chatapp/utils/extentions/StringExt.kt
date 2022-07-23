package com.karry.chatapp.utils.extentions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Patterns
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

fun String.fromHexString(): ByteArray {
    val hexString = this
    val hexLength = hexString.length
    val result = ByteArray(hexLength / 2)
    var i = 0
    while (i < hexLength) {
        val stringValue = hexString.substring(i, i + 2)
        val intValue = Integer.parseInt(stringValue, 16)
        result[i / 2] = intValue.toByte()
        i += 2
    }
    return result
}

private const val PASSWORD_PATTERN =
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"

fun String.passwordValidator(): Boolean {
    val pattern = Pattern.compile(PASSWORD_PATTERN)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}


fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.toTimeMiliSec(): Long {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return try {
        val date = format.parse(this)
        date?.time ?: 0
    } catch (e: ParseException) {
        0
    }
}

fun String .getBitmapFromURL(): Bitmap? {
    return try {
        val url = URL(this)
        val connection = url.openConnection()
        connection.doInput = true
        connection.connect()
        val input = connection.getInputStream()
        BitmapFactory.decodeStream(input)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private const val DATE_TIME_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val SOURCE_DATA_FORMATTER = "dd/MM/yy HH:mm"
fun String.toLocalDate(): String {
    val ha = SimpleDateFormat(DATE_TIME_FORMATTER, Locale.getDefault())
    val la = SimpleDateFormat(SOURCE_DATA_FORMATTER, Locale.getDefault())
    val date = ha.parse(this)!!
    return la.format(date)
}

fun String.toBase64String(): String {
    return Base64.getEncoder().encodeToString(this.toByteArray())
}

fun String.toBase64ByteArray(): ByteArray {
    return Base64.getEncoder().encode(this.toByteArray())
}

fun String.fromBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}