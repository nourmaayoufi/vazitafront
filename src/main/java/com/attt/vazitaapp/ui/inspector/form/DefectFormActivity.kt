package com.attt.vazitaapp.ui.inspector.form


import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.CarQueue
import com.attt.vazitaapp.ui.common.BaseActivity
import com.attt.vazitaapp.ui.inspector.form.viewmodel.DefectFormViewModel
import com.attt.vazitaapp.util.Constants

class DefectFormActivity : BaseActivity() {

    private lateinit var navController: NavController
    private val viewModel: DefectFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_defect_form)

        // Get car dossier from intent
        intent.getParcelableExtra<CarQueue>(Constants.EXTRA_CAR_DOSSIER)?.let {
            viewModel.setCarDossier(it)
        } ?: run {
            showError(getString(R.string.error_no_car_selected))
            finish()
            return
        }

        // Setup navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupToolbar()
        observeViewModel()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.title_defect_form)
        }
    }

    private fun observeViewModel() {
        viewModel.formCompleteEvent.observe(this) { isComplete ->
            if (isComplete) {
                setResult(RESULT_OK)
                finish()
            }
        }

        viewModel.errorEvent.observe(this) { error ->
            showError(error)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (navController.currentDestination?.id == R.id.chapterSelectionFragment) {
            // If we're at the first step, finish the activity
            finish()
            true
        } else {
            // Otherwise navigate up in the navigation hierarchy
            navController.navigateUp() || super.onSupportNavigateUp()
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.chapterSelectionFragment) {
            // If we're at the first step, finish the activity
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
