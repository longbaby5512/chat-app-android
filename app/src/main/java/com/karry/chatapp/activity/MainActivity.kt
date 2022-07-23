package com.karry.chatapp.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hoc081098.viewbindingdelegate.viewBinding
import com.karry.chatapp.NavGraphDirections
import com.karry.chatapp.R
import com.karry.chatapp.databinding.ActivityMainBinding
import com.karry.chatapp.ui.navigations.safeNavigate
import com.karry.chatapp.utils.storage.Storage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding by viewBinding<ActivityMainBinding>()
    private lateinit var navController: NavController

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
            if (navController.currentDestination?.id != R.id.loginFragment || navController.currentDestination?.id != R.id.splashFragment || navController.currentDestination?.id != R.id.homeFragment) {
                navController.popBackStack()
            }
        }
    }

    // Setup menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_logout -> {
                storage.clear()
                navController.safeNavigate(NavGraphDirections.actionToLoginFragment())
                navController.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}