package com.karry.chatapp.ui.chat.chat.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoc081098.viewbindingdelegate.inflateViewBinding
import com.karry.chatapp.databinding.ItemChatReceiverBinding
import com.karry.chatapp.databinding.ItemChatSenderBinding
import com.karry.chatapp.domain.model.Message
import com.karry.chatapp.utils.extentions.toLocalDate
import java.time.format.DateTimeFormatter

class ChatAdapter(private val me: Int, private val onClick: ((Message) -> Unit)) :
    ListAdapter<Message, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    class ChatSenderViewHolder(
        private val binding: ItemChatSenderBinding,
        private val onClick: ((Message) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding) {
                tvChatMessage.text = message.content
                tvChatTime.text = message.time.toLocalDate()
                root.setOnClickListener {
                    onClick(message)
                    if (tvChatTime.visibility == ViewGroup.VISIBLE) {
                        tvChatTime.visibility = ViewGroup.GONE
                    } else {
                        tvChatTime.visibility = ViewGroup.VISIBLE
                    }
                }
            }
        }
    }

    class ChatReceiverViewHolder(
        private val binding: ItemChatReceiverBinding,
        private val onClick: ((Message) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding) {
                tvChatMessage.text = message.content
                tvChatTime.text = message.time.toLocalDate()
                root.setOnClickListener {
                    onClick(message)
                    if (tvChatTime.visibility == ViewGroup.VISIBLE) {
                        tvChatTime.visibility = ViewGroup.GONE
                    } else {
                        tvChatTime.visibility = ViewGroup.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            ChatSenderViewHolder(parent inflateViewBinding false, onClick)
        } else {
            ChatReceiverViewHolder(parent inflateViewBinding false, onClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is ChatSenderViewHolder) {
            holder.bind(message)
        } else if (holder is ChatReceiverViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).from == me) {
            return VIEW_TYPE_MESSAGE_SENT
        }
        return VIEW_TYPE_MESSAGE_RECEIVED
    }

    companion object {
        const val VIEW_TYPE_MESSAGE_SENT = 1
        const val VIEW_TYPE_MESSAGE_RECEIVED = 2

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.from == newItem.from && oldItem.to == newItem.to
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.content == newItem.content
            }
        }

    }
}