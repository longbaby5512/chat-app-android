package com.karry.chatapp.ui.account.login

import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chaotic.Chaotic
import com.karry.chaotic.extentions.fromBase64Url
import com.karry.chaotic.extentions.toBase64Url
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentLoginBinding
import com.karry.chatapp.domain.model.Key
import com.karry.chatapp.ui.navigations.loginToHome
import com.karry.chatapp.ui.navigations.loginToSignUp
import com.karry.chatapp.utils.KEY_CURRENT_USER
import com.karry.chatapp.utils.KEY_SECRET_CRYPTO
import com.karry.chatapp.utils.KEY_USER_TOKEN
import com.karry.chatapp.utils.extentions.*
import com.karry.chatapp.utils.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.crypto.Cipher
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var storage: Storage

    @Inject
    lateinit var cypher: Chaotic

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
                loginToSignUp()
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
                    if (TextUtils.isEmpty(error)) {
                        if (error != null) {
                            toast(error)
                        }
                    }

                    if (user.isNotNull() && token.isNotNull() && !TextUtils.isEmpty(token)) {
                        storage.set(KEY_CURRENT_USER, user!!)
                        storage.set(KEY_USER_TOKEN, token!!)
                        storage.set(KEY_SECRET_CRYPTO, key!!)
                        ChatApplication.currentUser = user
                        ChatApplication.accessToken = token
                        ChatApplication.key = key
                        loginToHome()
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