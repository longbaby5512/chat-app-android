package com.karry.chatapp.utils.extentions

import timber.log.Timber

fun Any?.isNull() = this == null
fun Any?.isNotNull() = this != null

fun Any?.logD() = Timber.d(this.toString())
fun Any?.logE() = Timber.e(this.toString())
fun Any?.logI() = Timber.i(this.toString())
fun Any?.logW() = Timber.w(this.toString())
fun Any?.logV() = Timber.v(this.toString())
fun Any?.logWtf() = Timber.wtf(this.toString())
