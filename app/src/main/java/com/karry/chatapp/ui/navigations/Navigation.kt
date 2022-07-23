package com.karry.chatapp.ui.navigations

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.karry.chatapp.R
import com.karry.chatapp.domain.model.User
import com.karry.chatapp.ui.account.login.LoginFragmentDirections
import com.karry.chatapp.ui.chat.home.HomeFragmentDirections
import com.karry.chatapp.ui.chat.user_list.UserListFragmentDirections
import com.karry.chatapp.ui.splash.SplashFragmentDirections
import com.karry.chatapp.utils.extentions.onBackPressed
import timber.log.Timber

fun Fragment.loginToSignUp() {
    findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
}

fun Fragment.loginToHome() {
    findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
}

fun Fragment.splashToLogin() {
    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.loginFragment, true)
        .build()
    findNavController().safeNavigate(
        SplashFragmentDirections.actionSplashFragmentToLoginFragment(),
        navOptions
    )
}

fun Fragment.splashToHome() {
    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.homeFragment, true)
        .build()
    findNavController().safeNavigate(
        SplashFragmentDirections.actionSplashFragmentToHomeFragment(),
        navOptions
    )
}

fun Fragment.signUpToLogin() {
    onBackPressed()
}

fun Fragment.homeToUserList() {
    findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToUserListFragment())
}

fun Fragment.homeToChat(receiverUser: User) {
    findNavController().safeNavigate(
        HomeFragmentDirections.actionHomeFragmentToChatFragment(
            receiverUser
        )
    )
}

fun Fragment.userListToChat(receiverUser: User) {
    findNavController().safeNavigate(
        UserListFragmentDirections.actionUserListFragmentToChatFragment(
            receiverUser
        )
    )
}


fun NavController.safeNavigate(directions: NavDirections) {
    Timber.tag("NavController").d("Navigate to ${directions.actionId}")
    currentDestination?.getAction(directions.actionId)?.run {
        navigate(directions)
    }
}

fun NavController.safeNavigate(directions: NavDirections, navOptions: NavOptions) {
    Timber.tag("NavController").d("Navigate to ${directions.actionId}")
    currentDestination?.getAction(directions.actionId)?.run {
        navigate(directions, navOptions)
    }
}
