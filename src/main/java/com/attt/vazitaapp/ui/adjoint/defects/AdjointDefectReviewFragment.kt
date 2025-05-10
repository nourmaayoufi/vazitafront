package com.attt.vazitaapp.ui.adjoint.defects


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.attt.vazitaapp.databinding.FragmentDefectReviewBinding
import com.attt.vazitaapp.ui.admin.defects.DefectReviewAdapter
import com.attt.vazitaapp.ui.admin.defects.viewmodel.DefectReviewViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * The AdjointDefectReviewFragment reuses most of the functionality from the admin's
 * DefectReviewFragment but may have certain restrictions or different UI elements
 * based on the adjoint role's permissions.
 */
class AdjointDefectReviewFragment : BaseFragment() {

    private var _binding: FragmentDefectReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DefectReviewViewModel
    private lateinit var adapter: DefectReviewAdapter

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

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[DefectReviewViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        setupFilterOptions()
        observeDefects()

        // Initial load of defects
        viewModel.loadDefects()
    }

    private fun setupRecyclerView() {
        adapter = DefectReviewAdapter { dossierDefect ->
            // Navigate to defect detail view
            // Example: val action = AdjointDefectReviewFragmentDirections.actionToDefectDetail(dossierDefect.id)
            // findNavController().navigate(action)
        }

        binding.recyclerDefects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AdjointDefectReviewFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.setSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally handle live search
                return false
            }
        })
    }

    private fun setupFilterOptions() {
        // Setup date range picker
        binding.buttonDateFilter.setOnClickListener {
            // Show date range picker dialog
            // Use viewModel.setDateRange(startDate, endDate) after selection
        }

        // Setup inspector filter (if available for adjoint)
        binding.buttonInspectorFilter.setOnClickListener {
            // Show inspector selection dialog
            // Use viewModel.setInspectorId(inspectorId) after selection
        }

        // Setup reset filters button
        binding.buttonResetFilters.setOnClickListener {
            viewModel.resetFilters()
            binding.searchView.setQuery("", false)
        }
    }

    private fun observeDefects() {
        lifecycleScope.launch {
            viewModel.defectsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                // Show error message
                binding.errorView.visibility = View.VISIBLE
                binding.errorView.setErrorMessage(errorMessage)
            } else {
                binding.errorView.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}