package com.karry.chatapp.ui.chat.user_list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.ChatApplication.Companion.accessToken
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentUserListBinding
import com.karry.chatapp.ui.chat.user_list.adapter.UserAdapter
import com.karry.chatapp.utils.extentions.setTitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserListFragment : Fragment(R.layout.fragment_user_list) {

    private var adapter: UserAdapter? = null
    private val viewModel: UserListViewModel by viewModels()

    private val binding by viewBinding<FragmentUserListBinding>() {
        adapter = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        adapter = UserAdapter {
            findNavController().navigate(UserListFragmentDirections.actionUserListFragmentToChatFragment(it))
        }

        setTitle(ChatApplication.currentUser?.name ?: getString(R.string.app_name))


        val llm = LinearLayoutManager(context)
        binding.rvUsers.layoutManager = llm

        binding.rvUsers.adapter = adapter

        viewModel.getAllUsers(accessToken!!)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.state.collect { state ->
                with(state) {
                    loading(isLoading, isEmpty)
                    if (users.isNotEmpty()) {
                        adapter!!.submitList(users)
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("inflater.inflate(R.menu.menu_main, menu)", "com.karry.chatapp.R")
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
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