package com.attt.vazitaapp.ui.admin.users


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentUserDetailBinding
import com.attt.vazitaapp.ui.admin.users.viewmodel.UserManagementViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import java.text.SimpleDateFormat
import java.util.Locale

class UserDetailFragment : BaseFragment(), MenuProvider {

    private var _binding: FragmentUserDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserManagementViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()

        // Add menu provider
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[UserManagementViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.selectedUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                displayUserDetails(it)
            } ?: run {
                // If no user is selected, navigate back to the user list
                findNavController().navigateUp()
            }
        }

        viewModel.operationSuccessful.observe(viewLifecycleOwner) { successful ->
            if (successful) {
                Toast.makeText(requireContext(), "Operation completed successfully", Toast.LENGTH_SHORT).show()
                viewModel.resetOperationStatus()
                findNavController().navigateUp()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.resetOperationStatus()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun displayUserDetails(user: com.attt.vazitaapp.data.model.User) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        binding.apply {
            textViewUserIdValue.text = user.idUser
            textViewFirstNameValue.text = user.prenom
            textViewLastNameValue.text = user.nom
            textViewArabicFirstNameValue.text = user.prenomA ?: "-"
            textViewArabicLastNameValue.text = user.nomA ?: "-"

            val roleName = when (user.codGrp) {
                1 -> "Admin"
                2 -> "Inspector"
                3 -> "Adjoint"
                else -> "Unknown"
            }
            textViewRoleValue.text = roleName

            textViewCenterValue.text = user.idCentre.toString()

            val statusText = if (user.etat == 1) "Active" else "Inactive"
            textViewStatusValue.text = statusText

            // Format dates if available
            textViewStartDateValue.text = user.dateDeb?.let { dateFormat.format(it) } ?: "-"
            textViewEndDateValue.text = user.dateFin?.let { dateFormat.format(it) } ?: "-"

            // Set up edit button
            buttonEditUser.setOnClickListener {
                findNavController().navigate(R.id.action_userDetailFragment_to_userCreateEditFragment)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_user_detail, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete_user -> {
                confirmDeleteUser()
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    private fun confirmDeleteUser() {
        val user = viewModel.selectedUser.value ?: return

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_user))
            .setMessage(getString(R.string.delete_user_confirmation, user.prenom, user.nom))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteUser(user.idUser)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().removeMenuProvider(this)
        _binding = null
    }
}