package com.attt.vazitaapp.ui.admin.users



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.databinding.FragmentUserManagementBinding
import com.attt.vazitaapp.ui.admin.AdminMainActivity
import com.attt.vazitaapp.ui.admin.users.viewmodel.UserManagementViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.util.ViewUtils

class UserManagementFragment : BaseFragment() {

    private var _binding: FragmentUserManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserManagementViewModel
    private lateinit var adapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Load users when fragment is created
        viewModel.loadUsers()
    }

    private fun setupViewModel() {
        // In a real app, use a ViewModelFactory to inject dependencies
        viewModel = ViewModelProvider(requireActivity())[UserManagementViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = UserListAdapter { user ->
            // Handle user item click - show detail view
            viewModel.selectUser(user)
            findNavController().navigate(R.id.action_userManagementFragment_to_userDetailFragment)
        }

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = this@UserManagementFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
            binding.emptyView.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.resetOperationStatus()
            }
        }
    }

    private fun setupListeners() {
        binding.fabAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_userManagementFragment_to_userCreateEditFragment)
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter users - in a real app, this might call a repository method or filter in-memory
                filterUsers(newText.orEmpty())
                return true
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadUsers()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun filterUsers(query: String) {
        // This is a simple filter implementation
        // In a real app, you might want to do this in the ViewModel
        // or directly query the API with the filter
        viewModel.users.value?.let { allUsers ->
            if (query.isEmpty()) {
                adapter.submitList(allUsers)
            } else {
                val filteredList = allUsers.filter { user ->
                    user.nom.contains(query, ignoreCase = true) ||
                            user.prenom.contains(query, ignoreCase = true) ||
                            user.idUser.contains(query, ignoreCase = true)
                }
                adapter.submitList(filteredList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}