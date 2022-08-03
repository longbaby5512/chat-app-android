package com.karry.chatapp.ui.chat.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoc081098.viewbindingdelegate.inflateViewBinding
import com.karry.chatapp.databinding.ItemConversationBinding
import com.karry.chatapp.domain.model.Conversation
import com.karry.chatapp.domain.model.User
import com.karry.chatapp.utils.extentions.toLocalDate

class ConversationAdapter(private val onClick: (User)->Unit): ListAdapter<Conversation, ConversationAdapter.ConversationViewHolder>(DIFF_CALLBACK) {
    class ConversationViewHolder(private val binding: ItemConversationBinding, private val onClick: (User)->Unit): RecyclerView.ViewHolder(binding.root) {
        fun bind(conversation: Conversation) {
            with(binding) {
                tvConversationName.text = conversation.user.name
                tvConversationMessage.text = conversation.content
                tvConversationTime.text = conversation.timestamp.toLocalDate()
                root.setOnClickListener { onClick(conversation.user) }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(parent inflateViewBinding false, onClick)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Conversation>() {
            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
                return oldItem.content == newItem.content && oldItem.timestamp == newItem.timestamp
            }
            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
                return oldItem == newItem
            }
        }
    }
}