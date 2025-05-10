package com.attt.vazitaapp.ui.admin.analytics


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.AnalyticsData
import com.attt.vazitaapp.ui.admin.analytics.viewmodel.AnalyticsViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_chart.*

class ChartFragment : BaseFragment() {

    private val viewModel: AnalyticsViewModel by activityViewModels()
    private var chartType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chartType = it.getString("chartType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Set chart title based on chart type
        when (chartType) {
            "bar_chart" -> tvChartTitle.text = "Most Common Defects"
            "pie_chart" -> tvChartTitle.text = "Defects by Car Type"
            "line_chart" -> tvChartTitle.text = "Defects Over Time"
            "stacked_bar_chart" -> tvChartTitle.text = "Defect Distribution by Chapter"
            else -> tvChartTitle.text = "Analytics Chart"
        }

        // Setup back button
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Setup filter button
        btnFilter.setOnClickListener {
            findNavController().navigate(R.id.action_chart_to_filter)
        }

        // Setup export button
        btnExport.setOnClickListener {
            viewModel.exportAnalyticsData()
            showSnackbar("Exporting analytics data...")
        }
    }

    private fun observeViewModel() {
        viewModel.analyticsData.observe(viewLifecycleOwner) { data ->
            renderChart(data)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showSnackbar(it)
                errorView.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
                errorView.setErrorMessage(it)
            }
        }
    }

    private fun renderChart(data: AnalyticsData?) {
        if (data == null) {
            errorView.visibility = View.VISIBLE
            errorView.setErrorMessage("No data available")
            return
        }

        errorView.visibility = View.GONE

        // Make sure all chart containers are gone initially
        barChartContainer.visibility = View.GONE
        pieChartContainer.visibility = View.GONE
        lineChartContainer.visibility = View.GONE
        stackedBarChartContainer.visibility = View.GONE

        // Render the appropriate chart based on chart type
        when (chartType) {
            "bar_chart" -> renderBarChart(data)
            "pie_chart" -> renderPieChart(data)
            "line_chart" -> renderLineChart(data)
            "stacked_bar_chart" -> renderStackedBarChart(data)
        }
    }

    private fun renderBarChart(data: AnalyticsData) {
        barChartContainer.visibility = View.VISIBLE

        val barEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // Process data for the bar chart - most common defects
        data.commonDefects?.forEachIndexed { index, defect ->
            barEntries.add(BarEntry(index.toFloat(), defect.count.toFloat()))
            labels.add(defect.defectCode)
        }

        val barDataSet = BarDataSet(barEntries, "Defect Frequency").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
        }

        val barData = BarData(barDataSet)
        barChart.apply {
            this.data = barData
            description.isEnabled = false
            animateY(1000)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            axisRight.isEnabled = false
            legend.isEnabled = true
            invalidate()
        }
    }

    private fun renderPieChart(data: AnalyticsData) {
        pieChartContainer.visibility = View.VISIBLE

        val pieEntries = ArrayList<PieEntry>()

        // Process data for the pie chart - defects by car type
        data.defectsByCarType?.forEach { (carType, count) ->
            pieEntries.add(PieEntry(count.toFloat(), carType))
        }

        val pieDataSet = PieDataSet(pieEntries, "Car Types").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 14f
        }

        val pieData = PieData(pieDataSet)
        pieChart.apply {
            this.data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 35f
            transparentCircleRadius = 40f
            centerText = "Defects by\nCar Type"
            setCenterTextSize(14f)
            animateY(1000)
            legend.isEnabled = true
            invalidate()
        }
    }

    private fun renderLineChart(data: AnalyticsData) {
        lineChartContainer.visibility = View.VISIBLE

        val lineEntries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        // Process data for the line chart - defects over time
        data.defectsOverTime?.forEachIndexed { index, timePoint ->
            lineEntries.add(Entry(index.toFloat(), timePoint.count.toFloat()))
            labels.add(timePoint.date)
        }

        val lineDataSet = LineDataSet(lineEntries, "Defects Over Time").apply {
            color = resources.getColor(R.color.colorPrimary)
            valueTextSize = 12f
            lineWidth = 2f
            setCircleColor(resources.getColor(R.color.colorAccent))
            circleRadius = 4f
            setDrawCircleHole(true)
            setCircleHoleColor(resources.getColor(R.color.colorBackground))
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(lineDataSet)
        lineChart.apply {
            this.data = lineData
            description.isEnabled = false
            animateX(1000)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.labelCount = labels.size
            xAxis.labelRotationAngle = 45f
            axisRight.isEnabled = false
            legend.isEnabled = true
            invalidate()
        }
    }

    private fun renderStackedBarChart(data: AnalyticsData) {
        stackedBarChartContainer.visibility = View.VISIBLE

        val barChartSets = ArrayList<BarDataSet>()
        val labels = ArrayList<String>()

        // Process data for the stacked bar chart - defect distribution by chapter
        data.defectsByChapter?.forEach { (chapter, defects) ->
            val barEntries = ArrayList<BarEntry>()

            defects.forEachIndexed { index, defect ->
                barEntries.add(BarEntry(index.toFloat(), defect.count.toFloat()))

                // Add the label only once (for the first chapter)
                if (barChartSets.isEmpty()) {
                    labels.add(defect.defectPoint)
                }
            }

            val barDataSet = BarDataSet(barEntries, chapter).apply {
                // Assign a different color for each chapter
                color = ColorTemplate.COLORFUL_COLORS[barChartSets.size % ColorTemplate.COLORFUL_COLORS.size]
                valueTextSize = 10f
            }

            barChartSets.add(barDataSet)
        }

        val barData = BarData(barChartSets as List<IBarDataSet>?)
        stackedBarChart.apply {
            this.data = barData
            description.isEnabled = false
            animateY(1000)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = 45f
            axisRight.isEnabled = false
            legend.isEnabled = true
            // Set to stacked mode
            barData.isHighlightEnabled = false
            isDoubleTapToZoomEnabled = false
            invalidate()
        }
    }
}