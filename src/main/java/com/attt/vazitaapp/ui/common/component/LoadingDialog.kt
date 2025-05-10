package com.attt.vazitaapp.ui.common.component


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.attt.vazitaapp.R

/**
 * Dialog that shows a loading indicator.
 * Used throughout the app to indicate background processes.
 */
class LoadingDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove dialog title
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Set transparent background
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Create progress bar
        val progressBar = ProgressBar(context)

        // Set layout parameters
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // Create constraint layout as container
        val layout = ConstraintLayout(context)
        layout.layoutParams = params
        layout.addView(progressBar)

        // Set dialog content view
        setContentView(layout)

        // Make dialog not cancelable
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}