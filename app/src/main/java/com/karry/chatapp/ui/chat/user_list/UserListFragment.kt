package com.karry.chatapp.ui.chat.user_list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication.Companion.accessToken
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentUserListBinding
import com.karry.chatapp.ui.chat.user_list.adapter.UserAdapter
import com.karry.chatapp.ui.navigations.userListToChat
import com.karry.chatapp.utils.KEY_USER_TOKEN
import com.karry.chatapp.utils.extentions.toast
import com.karry.chatapp.utils.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class UserListFragment : Fragment(R.layout.fragment_user_list) {
    private val binding by viewBinding<FragmentUserListBinding>()
    private lateinit var adapter: UserAdapter
    private val viewModel: UserListViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter {
            userListToChat(it)
        }

        val llm = LinearLayoutManager(context)
        binding.rvUsers.layoutManager = llm

        binding.rvUsers.adapter = adapter

        viewModel.getAllUsers(accessToken!!)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.state.collect { state ->
                with(state) {
                    loading(isLoading, isEmpty)
                    if (users.isNotEmpty()) {
                        adapter.submitList(users)
                    }
                }
            }
        }
    }

    private fun loading(isLoading: Boolean, isEmpty: Boolean) {
        with(binding) {
            userListProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            userListEmptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE

            if (!isLoading && !isEmpty) {
                userListContainer.visibility = View.VISIBLE
            } else {
                userListContainer.visibility = View.GONE
            }
        }
    }
}