package com.karry.chatapp.ui.chat.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chaotic.Chaotic
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentHomeBinding
import com.karry.chatapp.ui.navigations.homeToUserList
import com.karry.chatapp.utils.extentions.showActionBar
import com.karry.chatapp.utils.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.crypto.Cipher
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    @Inject
    lateinit var storage: Storage

    @Inject
    lateinit var cypher: Chaotic

    private val binding by viewBinding<FragmentHomeBinding>() {
        btnNewMessage.setOnClickListener(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnNewMessage.setOnClickListener {
                homeToUserList()
            }
        }

        val origin = "HomeFragment"
        val key = "Key"
        Timber.d("Origin: $origin")
        cypher.init(Cipher.ENCRYPT_MODE, key.toByteArray())
        lifecycleScope.launchWhenCreated {
            val encrypted = cypher.doFinal(origin.toByteArray())
            Timber.d("Encrypted: ${String(encrypted)}")
            cypher.init(Cipher.DECRYPT_MODE, key.toByteArray())
            val decrypted = cypher.doFinal(encrypted)
            Timber.d("Decrypted: ${String(decrypted)}")

        }

    }

    override fun onResume() {
        super.onResume()
        showActionBar()
    }

}