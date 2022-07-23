package com.karry.chatapp.ui.chat.chat

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication.Companion.accessToken
import com.karry.chatapp.ChatApplication.Companion.currentUser
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentChatBinding
import com.karry.chatapp.domain.model.Message
import com.karry.chatapp.domain.model.MessageType
import com.karry.chatapp.domain.model.User
import com.karry.chatapp.ui.chat.chat.adapter.ChatAdapter
import com.karry.chatapp.utils.extentions.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val binding: FragmentChatBinding by viewBinding()
    private lateinit var adapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var messages: List<Message>

    private val args: ChatFragmentArgs by navArgs()
    private val toUser:User by lazy { args.receiverUser }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatAdapter(currentUser!!.id) {

        }

        viewModel.connectSocket()
        with(binding) {
            val llm = LinearLayoutManager(context)
            llm.stackFromEnd = true
            rvMessages.layoutManager = llm
            rvMessages.adapter = adapter
            loadMessages()
            btnSendMssage.setOnClickListener {
                sendMessage()
            }
            observeMessages()
        }
    }

    private fun sendMessage() {
        with(binding) {
            val message = edtChatMessage.text.toString()
            if (TextUtils.isEmpty(message)) {
                return
            }
            edtChatMessage.text?.clear()
            viewModel.sendMessage(message, toUser.id, MessageType.TEXT)
        }
    }

    private fun loadMessages() {
        viewModel.getAllMessages(accessToken!!, toUser.id, toUser.publicKey!!)

    }

    private fun observeMessages() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.state.collect { state ->
                with(state) {
                    loading(isLoading, isEmpty)
                    Timber.d("Messages: $messages")
                    if (messages.isNotEmpty()) {
                        this@ChatFragment.messages = messages
                        adapter.submitList(messages)
                    }

                    if (!TextUtils.isEmpty(error)) {
                        toast(error!!)
                    }
                }
            }
        }
    }

    private fun loading(isLoading: Boolean, isEmpty: Boolean) {
        with(binding) {
            chatLoadProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            chatEmptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE

            if (!isLoading && !isEmpty) {
                rvMessages.visibility = View.VISIBLE
            } else {
                rvMessages.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disconnectSocket()
    }
}