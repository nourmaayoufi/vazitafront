package com.attt.vazitaapp.ui.admin.analytics


import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.repository.AnalyticsRepository
import com.attt.vazitaapp.ui.admin.analytics.viewmodel.AnalyticsViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.util.DateUtils
import kotlinx.android.synthetic.main.fragment_analytics_filter.*
import java.util.*

class AnalyticsFilterFragment : BaseFragment() {

    private val viewModel: AnalyticsViewModel by activityViewModels()
    private var selectedCarType: String? = null
    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null
    private var selectedCenterId: Int? = null

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupDatePickers()
        loadCarTypes()
        loadCenters()

        // Apply any existing filters
        viewModel.analyticsData.value?.let { data ->
            // Set selected car type if exists
            data.carType?.let { carType ->
                selectedCarType = carType
                spinnerCarType.setSelection(
                    (spinnerCarType.adapter as ArrayAdapter<String>).getPosition(carType)
                )
            }

            // Set selected dates if exist
            data.startDate?.let { date ->
                selectedStartDate = date
                edtStartDate.setText(DateUtils.formatDate(date))
            }

            data.endDate?.let { date ->
                selectedEndDate = date
                edtEndDate.setText(DateUtils.formatDate(date))
            }

            // Set selected center if exists
            data.centerId?.let { id ->
                selectedCenterId = id
                // Find position of this center in the spinner
                val centerAdapter = spinnerCenter.adapter as ArrayAdapter<*>
                for (i in 0 until centerAdapter.count) {
                    val item = centerAdapter.getItem(i).toString()
                    if (item.startsWith("$id - ")) {
                        spinnerCenter.setSelection(i)
                        break
                    }
                }
            }
        }
    }

    private fun setupUI() {
        // Setup car type spinner
        spinnerCarType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Skip the "All Car Types" option
                    selectedCarType = parent?.getItemAtPosition(position).toString()
                } else {
                    selectedCarType = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCarType = null
            }
        }

        // Setup center spinner
        spinnerCenter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Skip the "All Centers" option
                    val centerString = parent?.getItemAtPosition(position).toString()
                    // Extract center ID from string format "ID - Center Name"
                    selectedCenterId = centerString.split(" - ")[0].toIntOrNull()
                } else {
                    selectedCenterId = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCenterId = null
            }
        }

        // Setup close button
        btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        // Setup apply button
        btnApply.setOnClickListener {
            applyFilters()
        }

        // Setup reset button
        btnReset.setOnClickListener {
            resetFilters()
        }
    }

    private fun setupDatePickers() {
        // Setup start date picker
        edtStartDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedStartDate = calendar.time
                edtStartDate.setText(DateUtils.formatDate(calendar.time))
            }, year, month, day).show()
        }

        // Setup end date picker
        edtEndDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedEndDate = calendar.time
                edtEndDate.setText(DateUtils.formatDate(calendar.time))
            }, year, month, day).show()
        }
    }

    private fun loadCarTypes() {
        val carTypes = mutableListOf("All Car Types")

        // Show loading
        progressBar.visibility = View.VISIBLE

        // Fetch car types from repository
        (viewModel as? AnalyticsRepository)?.getCarTypes()?.observe(viewLifecycleOwner) { types ->
            progressBar.visibility = View.GONE

            if (types != null) {
                carTypes.addAll(types)
            }

            // Setup spinner adapter
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                carTypes
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCarType.adapter = adapter
            }
        } ?: run {
            // If we can't get the ViewModel as repository, use dummy data for now
            progressBar.visibility = View.GONE

            carTypes.addAll(listOf("Sedan", "SUV", "Compact", "Truck", "Van"))

            // Setup spinner adapter
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                carTypes
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCarType.adapter = adapter
            }
        }
    }

    private fun loadCenters() {
        val centers = mutableListOf("All Centers")

        // Show loading
        progressBar.visibility = View.VISIBLE

        // Fetch centers from repository
        (viewModel as? AnalyticsRepository)?.getCenters()?.observe(viewLifecycleOwner) { centerList ->
            progressBar.visibility = View.GONE

            if (centerList != null) {
                // Format centers as "ID - Center Name"
                centers.addAll(centerList.map { "${it.id} - ${it.name}" })
            }

            // Setup spinner adapter
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                centers
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCenter.adapter = adapter
            }
        } ?: run {
            // If we can't get the ViewModel as repository, use dummy data for now
            progressBar.visibility = View.GONE

            centers.addAll(listOf(
                "1 - Center Benarous",
                "2 - Center Tunis",
                "3 - Center Sfax",
                "4 - Center Sousse"
            ))

            // Setup spinner adapter
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                centers
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCenter.adapter = adapter
            }
        }
    }

    private fun applyFilters() {
        // Validate date range if both dates are selected
        if (selectedStartDate != null && selectedEndDate != null) {
            if (selectedStartDate!!.after(selectedEndDate)) {
                showSnackbar("Start date cannot be after end date")
                return
            }
        }

        // Apply filters
        viewModel.loadAnalyticsData(
            carType = selectedCarType,
            startDate = selectedStartDate,
            endDate = selectedEndDate,
            centerId = selectedCenterId
        )

        // Navigate back
        findNavController().navigateUp()
    }

    private fun resetFilters() {
        // Reset UI
        spinnerCarType.setSelection(0)
        spinnerCenter.setSelection(0)
        edtStartDate.setText("")
        edtEndDate.setText("")

        // Reset values
        selectedCarType = null
        selectedStartDate = null
        selectedEndDate = null
        selectedCenterId = null

        // Reset filters in ViewModel
        viewModel.resetFilters()

        // Show confirmation
        showSnackbar("Filters reset successfully")
    }
}