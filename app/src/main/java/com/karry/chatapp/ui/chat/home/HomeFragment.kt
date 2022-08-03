package com.karry.chatapp.ui.chat.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.R
import com.karry.chatapp.databinding.FragmentHomeBinding
import com.karry.chatapp.ui.chat.home.adapter.ConversationAdapter
import com.karry.chatapp.utils.extentions.setTitle
import com.karry.chatapp.utils.extentions.showActionBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var adapter: ConversationAdapter? = null
    private val viewModel: HomeViewModel by activityViewModels()

    private val binding by viewBinding<FragmentHomeBinding>() {
        btnNewMessage.setOnClickListener(null)
        adapter = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        setTitle(ChatApplication.currentUser?.name ?: getString(R.string.app_name))

        adapter = ConversationAdapter() {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToChatFragment(it))
        }

        val llm = LinearLayoutManager(context)
        binding.rvConversations.layoutManager = llm
        binding.rvConversations.adapter = adapter

        with(binding) {
            btnNewMessage.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_userListFragment)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.state.collect { state ->
                Timber.d("state: $state")

                with(state) {
                    loading(isLoading, isEmpty)
                    if (conversations.isNotEmpty()) {
                        adapter!!.submitList(conversations)
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

    override fun onResume() {
        super.onResume()
        showActionBar()
    }

    private fun loading(isLoading: Boolean, isEmpty: Boolean) {
        with(binding) {
            homeProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            homeEmptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE

            btnNewMessage.visibility = if (isLoading) View.GONE else View.VISIBLE

            if (!isLoading && !isEmpty) {
                homeContainer.visibility = View.VISIBLE
            } else {
                homeContainer.visibility = View.GONE
            }
        }
    }


}