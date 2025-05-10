package com.attt.vazitaapp.ui.adjoint.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentAdjointDashboardBinding
import com.attt.vazitaapp.ui.admin.dashboard.DashboardItem
import com.attt.vazitaapp.ui.admin.dashboard.DashboardItemAdapter
import com.attt.vazitaapp.ui.admin.dashboard.DashboardItemType
import com.attt.vazitaapp.ui.common.BaseFragment

class AdjointDashboardFragment : BaseFragment() {

    private var _binding: FragmentAdjointDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashboardItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdjointDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadDashboardItems()
    }

    private fun setupRecyclerView() {
        adapter = DashboardItemAdapter { itemType ->
            when (itemType) {
                DashboardItemType.DEFECT_REVIEW -> navigateToDefectReview()
                DashboardItemType.ANALYTICS -> navigateToAnalytics()
                else -> { /* Ignore other types */ }
            }
        }

        binding.recyclerDashboardItems.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@AdjointDashboardFragment.adapter
        }
    }

    private fun loadDashboardItems() {
        // Note: Adjoint doesn't have access to user management, only defect review and analytics
        val items = listOf(
            DashboardItem(
                title = getString(R.string.defect_review),
                description = getString(R.string.defect_review_description),
                iconResId = R.drawable.ic_launcher, // Replace with actual icon
                type = DashboardItemType.DEFECT_REVIEW
            ),
            DashboardItem(
                title = getString(R.string.analytics),
                description = getString(R.string.analytics_description),
                iconResId = R.drawable.ic_launcher, // Replace with actual icon
                type = DashboardItemType.ANALYTICS
            )
        )

        adapter.submitList(items)
    }

    private fun navigateToDefectReview() {
        // Use Navigation Component to navigate to Defect Review
        // Example: findNavController().navigate(R.id.action_adjointDashboardFragment_to_adjointDefectReviewFragment)
    }

    private fun navigateToAnalytics() {
        // Use Navigation Component to navigate to Analytics
        // Example: findNavController().navigate(R.id.action_adjointDashboardFragment_to_analyticsDashboardFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}