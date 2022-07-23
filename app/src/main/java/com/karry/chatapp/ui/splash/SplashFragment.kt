package com.karry.chatapp.ui.splash

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentSplashBinding
import com.karry.chatapp.ui.navigations.splashToHome
import com.karry.chatapp.ui.navigations.splashToLogin
import com.karry.chatapp.utils.KEY_USER_TOKEN
import com.karry.chatapp.utils.extentions.hideActionBar
import com.karry.chatapp.utils.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private val binding by viewBinding(FragmentSplashBinding::bind)


    override fun onResume() {
        super.onResume()
        hideActionBar()
        // My looper
        thread {
            try {
                Thread.sleep(2000)
                Handler(Looper.getMainLooper()).post {
                    if (TextUtils.isEmpty(ChatApplication.accessToken)) {
                        findNavController().popBackStack(R.id.loginFragment, true)
                        splashToLogin()
                    } else {
                        findNavController().popBackStack(R.id.homeFragment, true)
                        splashToHome()
                    }
                }
            } catch (e: InterruptedException) {
                Timber.e(e)
            }
        }
    }
}