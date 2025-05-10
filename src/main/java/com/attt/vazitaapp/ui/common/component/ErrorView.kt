package com.attt.vazitaapp.ui.common.component


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.attt.vazitaapp.R

/**
 * Custom view to display error states with retry button.
 * Used throughout the app to indicate errors and provide a retry option.
 */
class ErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val errorIcon: ImageView
    private val errorTitle: TextView
    private val errorMessage: TextView
    private val retryButton: Button

    init {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.view_error, this, true)

        // Initialize views
        errorIcon = findViewById(R.id.errorIcon)
        errorTitle = findViewById(R.id.errorTitle)
        errorMessage = findViewById(R.id.errorMessage)
        retryButton = findViewById(R.id.retryButton)
    }

    /**
     * Set error details
     *
     * @param title The error title
     * @param message The error message
     * @param retryText The text for retry button
     * @param retryListener The click listener for retry button
     */
    fun setError(
        title: String,
        message: String,
        retryText: String = context.getString(R.string.retry),
        retryListener: OnClickListener? = null
    ) {
        errorTitle.text = title
        errorMessage.text = message
        retryButton.text = retryText

        if (retryListener != null) {
            retryButton.visibility = View.VISIBLE
            retryButton.setOnClickListener(retryListener)
        } else {
            retryButton.visibility = View.GONE
        }
    }

    /**
     * Set retry button click listener
     */
    fun setRetryClickListener(listener: OnClickListener) {
        retryButton.setOnClickListener(listener)
    }

    /**
     * Show the error view
     */
    fun show() {
        visibility = View.VISIBLE
    }

    /**
     * Hide the error view
     */
    fun hide() {
        visibility = View.GONE
    }
}