package com.karry.chatapp.utils.extentions

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.karry.chatapp.R

fun Activity.closeKeyboard() {
    this.currentFocus?.let {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.openKeyboard() {
    this.currentFocus?.let {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(it, 0)
    }
}

val Activity.isActionBarShown: Boolean
    get() = this.actionBar != null && this.actionBar!!.isShowing

fun AppCompatActivity.setDisplayHomeEnabled(showHomeAsUp: Boolean) {
    supportActionBar?.setHomeButtonEnabled(showHomeAsUp)
}