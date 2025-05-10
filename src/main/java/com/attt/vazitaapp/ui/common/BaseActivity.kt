package com.attt.vazitaapp.ui.common


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.attt.vazitaapp.data.local.TokenManager

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)

        // Check if token is valid, if not redirect to login
        if (!isAuthActivity() && !tokenManager.hasValidToken()) {
            redirectToLogin()
            return
        }
    }

    protected open fun isAuthActivity(): Boolean = false

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showLoading(isLoading: Boolean, loadingView: View) {
        loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    protected fun redirectToLogin() {
        // Clear token and navigate to login
        tokenManager.clearToken()
        // Intent to LoginActivity
        finish()
    }
}