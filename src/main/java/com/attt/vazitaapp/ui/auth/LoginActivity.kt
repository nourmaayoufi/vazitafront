package com.attt.vazitaapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.ui.admin.AdminMainActivity
import com.attt.vazitaapp.ui.adjoint.AdjointMainActivity
import com.attt.vazitaapp.ui.common.BaseActivity
import com.attt.vazitaapp.ui.inspector.InspectorMainActivity
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : BaseActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var tilUsername: TextInputLayout
    private lateinit var etUsername: EditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tilUsername = findViewById(R.id.tilUsername)
        etUsername = findViewById(R.id.etUsername)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                viewModel.login(username, password)
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            tilUsername.error = getString(R.string.error_username_required)
            isValid = false
        } else {
            tilUsername.error = null
        }

        if (password.isEmpty()) {
            tilPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else {
            tilPassword.error = null
        }

        return isValid
    }

    override fun setupObservers() {
        viewModel.loading.observe(this, Observer { isLoading ->
            showLoading(isLoading)
            btnLogin.isEnabled = !isLoading
        })

        viewModel.error.observe(this, Observer { error ->
            showError(error)
        })

        viewModel.loginResult.observe(this, Observer { user ->
            navigateToMainScreen(user)
        })
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