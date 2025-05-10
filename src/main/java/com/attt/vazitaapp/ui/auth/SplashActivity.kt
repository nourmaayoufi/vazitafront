package com.attt.vazitaapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.ui.admin.AdminMainActivity
import com.attt.vazitaapp.ui.adjoint.AdjointMainActivity
import com.attt.vazitaapp.ui.common.BaseActivity
import com.attt.vazitaapp.ui.inspector.InspectorMainActivity

class SplashActivity : BaseActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private val splashTimeout = 1500L // 1.5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check if user is already logged in after splash delay
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.checkLoggedInUser()
        }, splashTimeout)
    }

    override fun setupObservers() {
        viewModel.loading.observe(this, Observer { isLoading ->
            // Don't show loading indicator on splash screen
        })

        viewModel.error.observe(this, Observer { error ->
            // If there's an error checking login state, go to login
            navigateToLogin()
        })

        viewModel.user.observe(this, Observer { user ->
            if (user != null) {
                navigateToMainScreen(user)
            } else {
                navigateToLogin()
            }
        })
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToMainScreen(user: User) {
        val intent = when (user.roleCode) {
            1 -> Intent(this, AdminMainActivity::class.java) // ADMIN
            2 -> Intent(this, InspectorMainActivity::class.java) // INSPECTOR
            3 -> Intent(this, AdjointMainActivity::class.java) // ADJOINT
            else -> Intent(this, InspectorMainActivity::class.java) // Default to Inspector
        }
        startActivity(intent)
        finish()
    }
}
