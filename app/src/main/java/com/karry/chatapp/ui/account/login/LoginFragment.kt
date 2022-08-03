package com.karry.chatapp.ui.account.login

import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentLoginBinding
import com.karry.chatapp.utils.KEY_ACCESS_TOKEN
import com.karry.chatapp.utils.KEY_CURRENT_USER
import com.karry.chatapp.utils.KEY_REFRESH_TOKEN
import com.karry.chatapp.utils.KEY_SECRET_CRYPTO
import com.karry.chatapp.utils.extentions.*
import com.karry.chatapp.utils.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var storage: Storage

    private val loginViewModel: LoginViewModel by viewModels()


    private val binding by viewBinding<FragmentLoginBinding> {
        btnLogin.setOnClickListener(null)
        btnToSignUp.setOnClickListener(null)
        btnToForgot.setOnClickListener(null)
        btnLoginShowPassword.setOnClickListener(null)
        btnLoginFacebook.setOnClickListener(null)
        btnLoginGoogle.setOnClickListener(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnLoginShowPassword.setOnClickListener {
                onShowPasswordToggle()
            }

            btnToSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            btnToForgot.setOnClickListener {
                this@LoginFragment.toast("Forgot Password")
            }

            btnLoginFacebook.setOnClickListener {
                this@LoginFragment.toast("Facebook")
            }

            btnLoginGoogle.setOnClickListener {
                this@LoginFragment.toast("Google")
            }

            btnLogin.setOnClickListener {
                onLoginClick()
            }
        }


    }

    private fun onLoginClick() {
        with(binding) {
            closeKeyboard()

            val email = edtLoginEmail.text.trim().toString().lowercase()
            val password = edtLoginPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                toast("Please enter email and password")
                edtLoginEmail.requestFocus()
                return
            }

            if (TextUtils.isEmpty(email)) {
                toast("Please enter email")
                edtLoginEmail.requestFocus()
                return
            }

            if (TextUtils.isEmpty(password)) {
                toast("Please enter password")
                edtLoginPassword.requestFocus()
                return
            }


            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        loginViewModel.login(email, password)

        // Launch on Dispatchers.IO
        lifecycleScope.launch(Dispatchers.Main) {
            loginViewModel.state.collect {
                with(it) {
                    loading(isLoading)
                    if (error != null) {
                        toast(error)
                    }

                    if (user.isNotNull() && token.isNotNull() && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(refreshToken)) {
                        storage.set(KEY_CURRENT_USER, user!!)
                        storage.set(KEY_ACCESS_TOKEN, token!!)
                        storage.set(KEY_SECRET_CRYPTO, key!!)
                        storage.set(KEY_REFRESH_TOKEN, refreshToken!!)
                        ChatApplication.currentUser = user
                        ChatApplication.accessToken = token
                        ChatApplication.key = key
                        ChatApplication.refreshToken = refreshToken
                        findNavController().popBackStack()
                        val navOptions = navOptions {
                            popUpTo(R.id.homeFragment) {
                                inclusive = true
                            }
                        }
                        findNavController().navigate(R.id.homeFragment, null, navOptions)
                    }
                }
            }
        }
    }

    private fun onShowPasswordToggle() {
        with(binding) {
            if (edtLoginPassword.transformationMethod is PasswordTransformationMethod) {
                edtLoginPassword.transformationMethod = null
                btnLoginShowPassword.setImageResource(R.drawable.ic_hide_password)
                edtLoginPassword.setSelection(edtLoginPassword.text!!.length)
            } else {
                edtLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnLoginShowPassword.setImageResource(R.drawable.ic_show_password)
                edtLoginPassword.setSelection(edtLoginPassword.text!!.length)
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        with(binding) {
            loginProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            loginContainer.visibility = if (isLoading) View.GONE else View.VISIBLE

            if (isLoading) {
                edtLoginEmail.setText("")
                edtLoginPassword.setText("")
                edtLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnLoginShowPassword.setImageResource(R.drawable.ic_show_password)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar()
        with(binding) {
            edtLoginEmail.setText("")
            edtLoginPassword.setText("")
        }
    }

    override fun onStop() {
        super.onStop()
        showActionBar()
    }
}