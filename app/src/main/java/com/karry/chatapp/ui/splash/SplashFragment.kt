package com.karry.chatapp.ui.splash

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentSplashBinding
import com.karry.chatapp.utils.extentions.hideActionBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.concurrent.thread

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private val binding by viewBinding(FragmentSplashBinding::bind)
    private lateinit var savedStateHandle: SavedStateHandle

    override fun onResume() {
        super.onResume()
        hideActionBar()
        // My looper
        thread {
            try {
                Thread.sleep(2000)
                Handler(Looper.getMainLooper()).post {
                    if (TextUtils.isEmpty(ChatApplication.accessToken)) {
                        val navOptions = navOptions {
                            popUpTo(R.id.loginFragment) {
                                inclusive = true
                            }

                        }
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.loginFragment, null, navOptions)
                    } else {
                        val navOptions = navOptions {
                            popUpTo(R.id.homeFragment) {
                                inclusive = true
                            }
                        }
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.homeFragment, null, navOptions)
                    }
                }
            } catch (e: InterruptedException) {
                Timber.e(e)
            }
        }
    }
}