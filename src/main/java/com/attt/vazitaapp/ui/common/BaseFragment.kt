package com.attt.vazitaapp.ui.common

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.attt.vazitaapp.data.local.TokenManager

abstract class BaseFragment : Fragment() {

    protected lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(requireContext())
    }

    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    protected fun showLoading(isLoading: Boolean, loadingView: View) {
        loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    protected fun getBaseActivity(): BaseActivity? {
        return activity as? BaseActivity
    }
}