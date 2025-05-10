package com.attt.vazitaapp.ui.adjoint


import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.ActivityAdjointMainBinding
import com.attt.vazitaapp.ui.common.BaseActivity

class AdjointMainActivity : BaseActivity() {

    private lateinit var binding: ActivityAdjointMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdjointMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        // Set up the navigation controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_adjoint) as NavHostFragment
        navController = navHostFragment.navController

        // Define top-level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.adjointDashboardFragment
            )
        )

        // Set up the ActionBar with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up NavigationView if available (tablet landscape could have drawer navigation)
        binding.navViewAdjoint?.setupWithNavController(navController)

        // Set up bottom navigation if available (could be used in portrait mode)
        binding.bottomNavAdjoint?.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        // If at start destination, handle accordingly (e.g., show exit dialog)
        if (navController.currentDestination?.id == navController.graph.startDestinationId) {
            showExitDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showExitDialog() {
        // Show an exit confirmation dialog
        // For example:
        /*
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_message)
            .setPositiveButton(R.string.yes) { _, _ -> finish() }
            .setNegativeButton(R.string.no, null)
            .show()
        */
    }
}