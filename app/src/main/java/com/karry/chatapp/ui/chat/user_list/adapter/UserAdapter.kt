package com.karry.chatapp.ui.chat.user_list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hoc081098.viewbindingdelegate.inflateViewBinding
import com.karry.chatapp.databinding.ItemUserBinding
import com.karry.chatapp.domain.model.User

class UserAdapter(private val onClickListener: (User) -> Unit): ListAdapter<User, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    class UserViewHolder(
        private val binding: ItemUserBinding,
        private val onClickListener: (User) -> Unit
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email
            binding.root.setOnClickListener {
                onClickListener(user)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(parent inflateViewBinding  false, onClickListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}