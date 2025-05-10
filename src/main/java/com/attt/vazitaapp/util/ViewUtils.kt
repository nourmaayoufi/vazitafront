package com.attt.vazitaapp.util


import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Utility class for UI operations
 */
object ViewUtils {

    /**
     * Show a toast message
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    /**
     * Show a snackbar message
     */
    fun showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(view, message, duration).show()
    }

    /**
     * Show a snackbar message with action
     */
    fun showSnackbarWithAction(
        view: View,
        message: String,
        actionText: String,
        action: () -> Unit,
        duration: Int = Snackbar.LENGTH_LONG
    ) {
        Snackbar.make(view, message, duration)
            .setAction(actionText) { action.invoke() }
            .show()
    }

    /**
     * Hide keyboard
     */
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Show keyboard
     */
    fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * Hide keyboard for fragment
     */
    fun Fragment.hideKeyboard() {
        view?.let { ViewUtils.hideKeyboard(it) }
    }

    /**
     * Show a toast message from fragment
     */
    fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        context?.let { ViewUtils.showToast(it, message, duration) }
    }

    /**
     * Show a snackbar message from fragment
     */
    fun Fragment.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        view?.let { ViewUtils.showSnackbar(it, message, duration) }
    }
}