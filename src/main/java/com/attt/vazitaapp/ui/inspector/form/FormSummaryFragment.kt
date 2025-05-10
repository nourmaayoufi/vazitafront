package com.attt.vazitaapp.ui.inspector.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentFormSummaryBinding
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.ui.inspector.form.viewmodel.DefectFormViewModel

class FormSummaryFragment : BaseFragment() {

    private var _binding: FragmentFormSummaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DefectFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupButtons()
    }

    private fun observeViewModel() {
        viewModel.carDossier.observe(viewLifecycleOwner) { carDossier ->
            binding.textViewCarInfo.text = getString(
                R.string.car_info_format,
                carDossier.immatriculation,
                carDossier.numChassis
            )
        }

        viewModel.selectedAlterations.observe(viewLifecycleOwner) { alterations ->
            val summaryBuilder = StringBuilder()
            alterations.forEachIndexed { index, alteration ->
                val defectCode = "${alteration.codeChapter}${alteration.codePoint}${alteration.codeAlteration}"
                summaryBuilder.append("${index + 1}. ($defectCode) ${alteration.libelleAlteration}\n")
            }
            binding.textViewDefectSummary.text = summaryBuilder.toString()

            // Enable or disable submit button based on selection
            binding.buttonSubmit.isEnabled = alterations.isNotEmpty()
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonSubmit.isEnabled = !isLoading && (viewModel.selectedAlterations.value?.isNotEmpty() == true)
            binding.buttonBack.isEnabled = !isLoading
        }
    }

    private fun setupButtons() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSubmit.setOnClickListener {
            viewModel.submitForm()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}