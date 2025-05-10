package com.attt.vazitaapp.ui.admin.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentAdminDashboardBinding
import com.attt.vazitaapp.ui.common.BaseFragment

class AdminDashboardFragment : BaseFragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashboardItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
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
                DashboardItemType.USER_MANAGEMENT -> navigateToUserManagement()
                DashboardItemType.DEFECT_REVIEW -> navigateToDefectReview()
                DashboardItemType.ANALYTICS -> navigateToAnalytics()
            }
        }

        binding.recyclerDashboardItems.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@AdminDashboardFragment.adapter
        }
    }

    private fun loadDashboardItems() {
        val items = listOf(
            DashboardItem(
                title = getString(R.string.user_management),
                description = getString(R.string.user_management_description),
                iconResId = R.drawable.ic_launcher, // Replace with actual icon
                type = DashboardItemType.USER_MANAGEMENT
            ),
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

    private fun navigateToUserManagement() {
        // Use Navigation Component to navigate to User Management
        // Based on nav_admin.xml defined in your project
        // Example: findNavController().navigate(R.id.action_adminDashboardFragment_to_userManagementFragment)
    }

    private fun navigateToDefectReview() {
        // Use Navigation Component to navigate to Defect Review
        // Example: findNavController().navigate(R.id.action_adminDashboardFragment_to_defectReviewFragment)
    }

    private fun navigateToAnalytics() {
        // Use Navigation Component to navigate to Analytics
        // Example: findNavController().navigate(R.id.action_adminDashboardFragment_to_analyticsDashboardFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}