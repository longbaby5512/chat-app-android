package com.karry.chatapp.utils.extentions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.onBackPressed() {
    requireActivity().onBackPressed()
}

fun Fragment.hideActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.hide()
}

fun Fragment.showActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.show()
}

val Fragment.isActionBarShown: Boolean
    get() = requireActivity().isActionBarShown

fun Fragment.finish() {
    requireActivity().finish()
}

fun Fragment.setTitle(title: String) {
    (requireActivity() as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.toast(message: String) {
    requireActivity().toast(message)
}

fun Fragment.closeKeyboard() {
    requireActivity().closeKeyboard()
}

fun Fragment.openKeyboard() {
    requireActivity().openKeyboard()
}

fun Fragment.setDisplayHomeEnabled(showHomeAsUp: Boolean) {
    (requireActivity() as AppCompatActivity).setDisplayHomeEnabled(showHomeAsUp)
}