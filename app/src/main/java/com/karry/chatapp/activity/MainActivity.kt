package com.karry.chatapp.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.ChatApplication
import com.karry.chatapp.R
import com.karry.chatapp.databinding.ActivityMainBinding
import com.karry.chatapp.utils.extentions.toast
import com.karry.chatapp.utils.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding<ActivityMainBinding>()
    private lateinit var navController: NavController

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var storage: Storage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.signUpFragment || destination.id == R.id.homeFragment) {
                binding.toolbar.navigationIcon = null
            } else {
                binding.toolbar.setNavigationIcon(R.drawable.ic_back)
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        viewModel.logout(ChatApplication.accessToken!!)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collect {
                loading(it.isLoading)
                if (it.isSuccess) {
                    storage.clear()

                    val navOptions = navOptions {
                        popUpTo(R.id.homeFragment) {
                            inclusive = true
                        }
                    }
                    navController.clearBackStack(R.id.homeFragment)
                    navController.navigate(R.id.loginFragment, null, navOptions)
                    ChatApplication.clear()
                }
                if (it.error != null) {
                    toast(it.error)
                }
            }
        }

    }

    fun loading(isLoading: Boolean) {
        binding.contentMain.loadingLogout.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentMain.navHostFragmentContentMain.visibility = if (isLoading) View.GONE else View.VISIBLE
        supportActionBar?.hide()
    }

}