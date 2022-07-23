package com.karry.chaotic

import javax.crypto.Cipher


enum class ChaoticType(val type: Int) {
    Logistic(0),
    Sin(1);

    override fun toString(): String {
        return if (this == Logistic) {
            "Logistic"
        } else {
            "Sin"
        }
    }
}

class Chaotic private constructor(
    private val permType: ChaoticType = ChaoticType.Logistic,
    private val subType: ChaoticType = ChaoticType.Logistic,
    private val diffType: ChaoticType = ChaoticType.Logistic
) {

    private lateinit var key: ByteArray
    private var mode = Cipher.ENCRYPT_MODE

    fun init(mode: Int, key: ByteArray) {
        this.mode = mode
        this.key = key
    }

    fun doFinal(data: ByteArray): ByteArray {
        return if (mode == Cipher.ENCRYPT_MODE) {
            encrypt(data)
        } else {
            decrypt(data)
        }
    }

    private fun decrypt(data: ByteArray): ByteArray {
        return NativeLib.instance.decrypt(data, key, permType, subType, diffType)
    }

    private fun encrypt(data: ByteArray): ByteArray {
        return NativeLib.instance.encrypt(data, key, permType, subType, diffType)
    }


    companion object {

        private var INSTANCE: Chaotic? = null


        fun getInstance(
            permType: ChaoticType = ChaoticType.Logistic,
            subType: ChaoticType = ChaoticType.Logistic,
            diffType: ChaoticType = ChaoticType.Logistic
        ): Chaotic {
            if (INSTANCE == null) {
                INSTANCE = Chaotic(permType, subType, diffType)
            }
            return INSTANCE!!
        }

    }
}

private class NativeLib {
    external fun encrypt(
        data: ByteArray,
        key: ByteArray,
        perm: ChaoticType,
        sub: ChaoticType,
        diff: ChaoticType
    ): ByteArray

    external fun decrypt(
        data: ByteArray,
        key: ByteArray,
        perm: ChaoticType,
        sub: ChaoticType,
        diff: ChaoticType
    ): ByteArray

    companion object {
        init {
            System.loadLibrary("chaotic")
        }

        val instance: NativeLib = NativeLib()
    }
}