package com.attt.vazitaapp.ui.admin.defects


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentDefectReviewBinding
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.ui.admin.defects.viewmodel.DefectReviewViewModel
import java.util.Date

class DefectReviewFragment : BaseFragment() {

    private var _binding: FragmentDefectReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DefectReviewViewModel
    private lateinit var adapter: DefectReviewAdapter

    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefectReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSearch()
        setupFilters()
        setupRefreshLayout()

        loadInitialDefects()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[DefectReviewViewModel::class.java]

        viewModel.defects.observe(viewLifecycleOwner) { defects ->
            if (defects.isEmpty() && currentPage == 0) {
                binding.emptyView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                if (currentPage == 0) {
                    adapter.submitList(defects)
                } else {
                    val currentList = adapter.currentList.toMutableList()
                    currentList.addAll(defects)
                    adapter.submitList(currentList)
                }

                isLastPage = defects.size < pageSize
                isLoading = false
                binding.loadingIndicator.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.loadingIndicator.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                isLoading = false
                showErrorView(it) {
                    if (currentPage == 0) {
                        loadInitialDefects()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = DefectReviewAdapter { dossierDefect ->
            // Navigate to defect detail
            val action = DefectReviewFragmentDirections
                .actionDefectReviewFragmentToDefectDetailFragment(dossierDefect.nDossier)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DefectReviewFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize
                        ) {
                            loadMoreDefects()
                        }
                    }
                }
            })
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                applySearchAndFilters()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    applySearchAndFilters()
                }
                return true
            }
        })
    }

    private fun setupFilters() {
        binding.btnFilterDate.setOnClickListener {
            // Show date range picker dialog
            val dateRangePickerDialog = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.select_date_range))
                .build()

            dateRangePickerDialog.addOnPositiveButtonClickListener { selection ->
                val startDate = Date(selection.first)
                val endDate = Date(selection.second)

                binding.tvDateRange.text = "${startDate.formatDateForUI()} - ${endDate.formatDateForUI()}"
                viewModel.setDateRange(startDate, endDate)
                resetAndReload()
            }

            dateRangePickerDialog.addOnNegativeButtonClickListener {
                // Clear date filter
                binding.tvDateRange.text = getString(R.string.all_dates)
                viewModel.clearDateRange()
                resetAndReload()
            }

            dateRangePickerDialog.show(childFragmentManager, "DATE_RANGE_PICKER")
        }

        binding.btnFilterInspector.setOnClickListener {
            // Show inspector selection dialog
            viewModel.loadInspectors().observe(viewLifecycleOwner) { inspectors ->
                val inspectorNames = inspectors.map { "${it.nom} ${it.prenom}" }.toTypedArray()

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.select_inspector))
                    .setItems(inspectorNames) { _, which ->
                        val selectedInspector = inspectors[which]
                        binding.tvInspector.text = "${selectedInspector.nom} ${selectedInspector.prenom}"
                        viewModel.setInspector(selectedInspector.matAgent)
                        resetAndReload()
                    }
                    .setNegativeButton(getString(R.string.clear)) { _, _ ->
                        binding.tvInspector.text = getString(R.string.all_inspectors)
                        viewModel.clearInspector()
                        resetAndReload()
                    }
                    .show()
            }
        }

        binding.btnClearFilters.setOnClickListener {
            clearFilters()
            resetAndReload()
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            resetAndReload()
        }
    }

    private fun loadInitialDefects() {
        currentPage = 0
        isLoading = true
        isLastPage = false
        binding.loadingIndicator.visibility = View.VISIBLE

        val query = binding.searchView.query.toString()
        viewModel.loadDefects(query, currentPage, pageSize)
    }

    private fun loadMoreDefects() {
        isLoading = true
        currentPage++
        binding.loadingIndicator.visibility = View.VISIBLE

        val query = binding.searchView.query.toString()
        viewModel.loadDefects(query, currentPage, pageSize)
    }

    private fun applySearchAndFilters() {
        resetAndReload()
    }

    private fun resetAndReload() {
        currentPage = 0
        isLoading = true
        isLastPage = false
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.recyclerView.scrollToPosition(0)

        val query = binding.searchView.query.toString()
        viewModel.loadDefects(query, currentPage, pageSize)
    }

    private fun clearFilters() {
        binding.tvDateRange.text = getString(R.string.all_dates)
        binding.tvInspector.text = getString(R.string.all_inspectors)
        binding.searchView.setQuery("", false)

        viewModel.clearFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}