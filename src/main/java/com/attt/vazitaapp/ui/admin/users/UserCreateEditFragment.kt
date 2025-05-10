package com.attt.vazitaapp.ui.admin.users


import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.databinding.FragmentUserCreateEditBinding
import com.attt.vazitaapp.ui.admin.users.viewmodel.UserManagementViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.util.DateUtils
import java.util.Calendar
import java.util.Date

class UserCreateEditFragment : BaseFragment() {

    private var _binding: FragmentUserCreateEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserManagementViewModel
    private var isEditMode = false
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserCreateEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRoleSpinner()
        setupDatePickers()
        setupObservers()
        setupListeners()

        // Determine if we're in edit mode or create mode
        determineMode()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[UserManagementViewModel::class.java]
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("Admin", "Inspector", "Adjoint")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()

        binding.editTextStartDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    startDate = calendar.time
                    binding.editTextStartDate.setText(DateUtils.formatDate(startDate!!))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.editTextEndDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    endDate = calendar.time
                    binding.editTextEndDate.setText(DateUtils.formatDate(endDate!!))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupObservers() {
        viewModel.selectedUser.observe(viewLifecycleOwner) { user ->
            if (isEditMode && user != null) {
                populateForm(user)
            }
        }

        viewModel.operationSuccessful.observe(viewLifecycleOwner) { successful ->
            if (successful) {
                val message = if (isEditMode) "User updated successfully" else "User created successfully"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
            binding.buttonSave.isEnabled = !isLoading
        }
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            if (validateForm()) {
                saveUser()
            }
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun determineMode() {
        isEditMode = viewModel.selectedUser.value != null

        // Update UI based on mode
        binding.textViewTitle.text = getString(
            if (isEditMode) R.string.edit_user else R.string.create_user
        )

        // User ID field should be enabled only in create mode
        binding.editTextUserId.isEnabled = !isEditMode
    }

    private fun populateForm(user: User) {
        binding.apply {
            editTextUserId.setText(user.idUser)
            editTextPassword.setText(user.passe)
            editTextFirstName.setText(user.prenom)
            editTextLastName.setText(user.nom)
            editTextArabicFirstName.setText(user.prenomA)
            editTextArabicLastName.setText(user.nomA)

            // Set spinner position based on role code
            val rolePosition = when (user.codGrp) {
                1 -> 0 // Admin
                2 -> 1 // Inspector
                3 -> 2 // Adjoint
                else -> 0 // Default to Admin
            }
            spinnerRole.setSelection(rolePosition)

            editTextCenter.setText(user.idCentre.toString())

            // Set dates
            user.dateDeb?.let {
                startDate = it
                editTextStartDate.setText(DateUtils.formatDate(it))
            }

            user.dateFin?.let {
                endDate = it
                editTextEndDate.setText(DateUtils.formatDate(it))
            }

            // Set status
            switchStatus.isChecked = user.etat == 1
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        // Clear all error messages first
        binding.apply {
            textInputLayoutUserId.error = null
            textInputLayoutPassword.error = null
            textInputLayoutFirstName.error = null
            textInputLayoutLastName.error = null
            textInputLayoutCenter.error = null
        }

        // Validate user ID
        if (binding.editTextUserId.text.toString().trim().isEmpty()) {
            binding.textInputLayoutUserId.error = getString(R.string.field_required)
            valid = false
        }

        // Validate password
        if (binding.editTextPassword.text.toString().isEmpty()) {
            binding.textInputLayoutPassword.error = getString(R.string.field_required)
            valid = false
        }

        // Validate first name
        if (binding.editTextFirstName.text.toString().trim().isEmpty()) {
            binding.textInputLayoutFirstName.error = getString(R.string.field_required)
            valid = false
        }

        // Validate last name
        if (binding.editTextLastName.text.toString().trim().isEmpty()) {
            binding.textInputLayoutLastName.error = getString(R.string.field_required)
            valid = false
        }

        // Validate center
        if (binding.editTextCenter.text.toString().trim().isEmpty()) {
            binding.textInputLayoutCenter.error = getString(R.string.field_required)
            valid = false
        }

        return valid
    }

    private fun saveUser() {
        val userId = binding.editTextUserId.text.toString().trim()
        val password = binding.editTextPassword.text.toString()
        val firstName = binding.editTextFirstName.text.toString().trim()
        val lastName = binding.editTextLastName.text.toString().trim()
        val arabicFirstName = binding.editTextArabicFirstName.text.toString().trim().takeIf { it.isNotEmpty() }
        val arabicLastName = binding.editTextArabicLastName.text.toString().trim().takeIf { it.isNotEmpty() }

        // Get role code from spinner position
        val roleCode = when (binding.spinnerRole.selectedItemPosition) {
            0 -> 1 // Admin
            1 -> 2 // Inspector
            2 -> 3 // Adjoint
            else -> 1 // Default to Admin
        }

        val centerId = binding.editTextCenter.text.toString().trim().toIntOrNull() ?: 0
        val status = if (binding.switchStatus.isChecked) 1 else 0

        val user = User(
            idUser = userId,
            passe = password,
            nom = lastName,
            prenom = firstName,
            nomA = arabicLastName,
            prenomA = arabicFirstName,
            codGrp = roleCode,
            idCentre = centerId,
            dateDeb = startDate,
            dateFin = endDate,
            etat = status
        )

        if (isEditMode) {
            viewModel.updateUser(user)
        } else {
            viewModel.createUser(user)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}