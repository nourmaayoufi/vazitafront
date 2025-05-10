package com.attt.vazitaapp.ui.admin.defects


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentDefectDetailBinding
import com.attt.vazitaapp.ui.admin.defects.viewmodel.DefectReviewViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.util.DateUtils

class uDefectDetailFragment : BaseFragment() {

    private var _binding: FragmentDefectDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DefectReviewViewModel
    private val args: DefectDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefectDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        loadDefectDetails()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[DefectReviewViewModel::class.java]

        viewModel.defectDetail.observe(viewLifecycleOwner) { defect ->
            binding.apply {
                progressBar.visibility = View.GONE
                contentLayout.visibility = View.VISIBLE

                tvDossierNumber.text = defect.nDossier
                tvChassis.text = defect.numChassis
                tvRegistration.text = defect.immatriculation ?: "-"

                // Format defect code to display chapter-point-alteration
                val defectCode = defect.codeDefaut.toString().padStart(3, '0')
                val chapter = defectCode.substring(0, 1)
                val point = defectCode.substring(1, 2)
                val alteration = defectCode.substring(2, 3)

                tvDefectCode.text = defectCode
                tvChapter.text = "${defect.chapitre?.libelleChapter ?: "Chapter $chapter"}"
                tvPoint.text = "${defect.point?.libellePoint ?: "Point $point"}"
                tvAlteration.text = "${defect.alteration?.libelleAlteration ?: "Alteration $alteration"}"

                tvInspector.text = defect.inspectorName ?: defect.matAgent
                tvDate.text = DateUtils.formatDateTimeForUI(defect.dateHeureEnregistrement)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.progressBar.visibility = View.GONE
                showErrorView(it) {
                    loadDefectDetails()
                }
            }
        }
    }

    private fun loadDefectDetails() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        viewModel.loadDefectDetail(args.dossierNumber)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_defect_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                // Navigate to edit fragment
                val action = DefectDetailFragmentDirections
                    .actionDefectDetailFragmentToDefectEditFragment(args.dossierNumber)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}