package com.karry.chaotic

import android.util.Base64
import java.security.MessageDigest

object Hash {
    /**
     * Generates a SHA256 hash of the given string.
     * @param input The string to hash.
     * @return The hash of the string in base64.
     */
    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray())
        return Base64.encodeToString(hash, Base64.DEFAULT)
    }
}