package com.karry.chatapp.ui.account.sign_up

import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentSignUpBinding
import com.karry.chatapp.utils.extentions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val binding by viewBinding<FragmentSignUpBinding> {
        btnSignUp.setOnClickListener(null)
        btnSignUpFacebook.setOnClickListener(null)
        btnSignUpGoogle.setOnClickListener(null)
        btnToLogin.setOnClickListener(null)
        btnSignUpShowPassword.setOnClickListener(null)
    }

    private val signUpViewModel by viewModels<SignUpViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnToLogin.setOnClickListener {
                activity?.onBackPressed()
            }

            btnSignUpShowPassword.setOnClickListener {
                onShowPasswordToggle()
            }

            btnSignUpFacebook.setOnClickListener {
                toast("Sign up with Facebook")
            }

            btnSignUpGoogle.setOnClickListener {
                toast("Sign up with Google")
            }

            btnSignUp.setOnClickListener {
                onSignUpClick()
            }
        }

    }

    private fun onSignUpClick() {
        with(binding) {
            val name = edtSignUpName.text.toString()
            val email = edtSignUpEmail.text.toString().lowercase().trim()
            val password = edtSignUpPassword.text.toString()

            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                toast("Please enter all fields")
                edtSignUpName.requestFocus()
                return
            }

            if (TextUtils.isEmpty(name)) {
                toast("Please enter name")
                edtSignUpName.requestFocus()
                return
            }

            if (TextUtils.isEmpty(email)) {
                toast("Please enter email")
                edtSignUpEmail.requestFocus()
                return
            }

            if (!email.isEmail()) {
                toast("Please enter valid email")
                edtSignUpEmail.requestFocus()
                return
            }

            if (TextUtils.isEmpty(password)) {
                toast("Please enter password")
                edtSignUpPassword.requestFocus()
                return
            }

//            if (!password.passwordValidator()) {
//                toast("Password is too weak")
//                edtSignUpPassword.requestFocus()
//                return
//            }

            signUp(name, email, password)

        }
    }

    private fun signUp(name: String, email: String, password: String) {
        signUpViewModel.signUp(name, email, password)
        lifecycleScope.launch(Dispatchers.Main) {
            signUpViewModel.state.collect { state ->
                with(state) {
                    loading(isLoading)
                    if (isSuccess) {
                        toast("Sign up success")
                        activity?.onBackPressed()
                    }

                    if (error != null) {
                        toast(error)
                    }
                }

            }
        }
    }

    private fun onShowPasswordToggle() {
        with(binding) {
            if (edtSignUpPassword.transformationMethod is PasswordTransformationMethod) {
                edtSignUpPassword.transformationMethod = null
                btnSignUpShowPassword.setImageResource(R.drawable.ic_hide_password)
                // Move cursor to the end of text
                edtSignUpPassword.setSelection(edtSignUpPassword.text!!.length)
            } else {
                edtSignUpPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnSignUpShowPassword.setImageResource(R.drawable.ic_show_password)
                // Move cursor to the end of text
                edtSignUpPassword.setSelection(edtSignUpPassword.text!!.length)
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        with(binding) {
            signUpProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            signUpContainer.visibility = if (isLoading) View.GONE else View.VISIBLE

            if (isLoading) {
                edtSignUpName.setText("")
                edtSignUpEmail.setText("")
                edtSignUpPassword.setText("")
                edtSignUpPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnSignUpShowPassword.setImageResource(R.drawable.ic_show_password)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        hideActionBar()
    }

}