package com.attt.vazitaapp.ui.admin.analytics


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.AnalyticsData
import com.attt.vazitaapp.ui.admin.analytics.viewmodel.AnalyticsViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.ui.admin.dashboard.DashboardItemAdapter
import kotlinx.android.synthetic.main.fragment_analytics_dashboard.*
import java.util.Date

class AnalyticsDashboardFragment : BaseFragment() {

    private val viewModel: AnalyticsViewModel by activityViewModels()
    private lateinit var adapter: DashboardItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Setup RecyclerView
        adapter = DashboardItemAdapter { chartType ->
            // Navigate to the specific chart fragment
            val bundle = Bundle().apply {
                putString("chartType", chartType)
            }
            findNavController().navigate(R.id.action_analytics_dashboard_to_chart, bundle)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AnalyticsDashboardFragment.adapter
        }

        // Setup refresh functionality
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }

        // Setup filter button
        btnFilter.setOnClickListener {
            findNavController().navigate(R.id.action_analytics_dashboard_to_filter)
        }

        // Setup export button
        btnExport.setOnClickListener {
            viewModel.exportAnalyticsData()
            showSnackbar("Exporting analytics data...")
        }
    }

    private fun observeViewModel() {
        viewModel.analyticsData.observe(viewLifecycleOwner) { data ->
            updateDashboard(data)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showSnackbar(it)
                errorView.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
                errorView.setErrorMessage(it)
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { message ->
            message?.let {
                showSnackbar(it)
            }
        }
    }

    private fun updateDashboard(data: AnalyticsData?) {
        if (data == null) {
            errorView.visibility = View.VISIBLE
            errorView.setErrorMessage("No data available")
            return
        }

        errorView.visibility = View.GONE

        // Update filter information display
        tvFilterInfo.text = buildFilterInfoText(data)

        // Create dashboard items
        val dashboardItems = listOf(
            "Most Common Defects" to "bar_chart",
            "Defects by Car Type" to "pie_chart",
            "Defects Over Time" to "line_chart",
            "Defect Distribution by Chapter" to "stacked_bar_chart"
        )

        adapter.submitList(dashboardItems)
    }

    private fun buildFilterInfoText(data: AnalyticsData): String {
        val filterParts = mutableListOf<String>()

        data.carType?.let {
            filterParts.add("Car Type: $it")
        }

        data.startDate?.let { start ->
            data.endDate?.let { end ->
                filterParts.add("Period: ${formatDate(start)} - ${formatDate(end)}")
            }
        }

        data.centerName?.let {
            filterParts.add("Center: $it")
        }

        return if (filterParts.isEmpty()) {
            "All Data"
        } else {
            filterParts.joinToString(" | ")
        }
    }

    private fun formatDate(date: Date): String {
        // Format date for display
        return android.text.format.DateFormat.format("dd/MM/yyyy", date).toString()
    }
}