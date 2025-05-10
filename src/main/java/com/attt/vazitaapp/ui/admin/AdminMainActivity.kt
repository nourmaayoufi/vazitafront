package com.attt.vazitaapp.ui.admin


import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.local.TokenManager
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.ui.auth.LoginActivity
import com.attt.vazitaapp.ui.common.BaseActivity
import com.attt.vazitaapp.util.Constants
import com.attt.vazitaapp.databinding.ActivityAdminMainBinding
import com.google.android.material.navigation.NavigationView
import android.content.Intent
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

class AdminMainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var tokenManager: TokenManager
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        setupCurrentUser()
        setupToolbar()
        setupNavigation()
    }

    private fun setupCurrentUser() {
        // In a real app, you would get the user from the intent or from a repository
        val userId = tokenManager.getUserId()
        val userName = tokenManager.getUserName()
        val centerName = tokenManager.getCenterName()

        if (userId == null || userName == null) {
            logout()
            return
        }

        currentUser = User(
            id = userId,
            name = userName,
            centerName = centerName ?: "Unknown Center"
        )

        // Update navigation header with user info
        val headerView = binding.navView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<android.widget.TextView>(R.id.nav_header_user_name)
        val centerTextView = headerView.findViewById<android.widget.TextView>(R.id.nav_header_center_name)

        userNameTextView.text = currentUser.name
        centerTextView.text = currentUser.centerName
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.appBarLayout.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupNavigation() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment)

        // Define top level destinations (no back button)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dashboard,
                R.id.nav_user_management,
                R.id.nav_defect_review,
                R.id.nav_analytics
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.appBarLayout.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                if (navController.currentDestination?.id != R.id.nav_dashboard) {
                    navController.navigate(R.id.nav_dashboard)
                }
            }
            R.id.nav_user_management -> {
                if (navController.currentDestination?.id != R.id.nav_user_management) {
                    navController.navigate(R.id.nav_user_management)
                }
            }
            R.id.nav_defect_review -> {
                if (navController.currentDestination?.id != R.id.nav_defect_review) {
                    navController.navigate(R.id.nav_defect_review)
                }
            }
            R.id.nav_analytics -> {
                if (navController.currentDestination?.id != R.id.nav_analytics) {
                    navController.navigate(R.id.nav_analytics)
                }
            }
            R.id.nav_logout -> {
                logout()
                return true
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        tokenManager.clearTokens()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}