package com.attt.vazitaapp.ui.admin.defects


import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentDefectEditBinding
import com.attt.vazitaapp.ui.admin.defects.viewmodel.DefectReviewViewModel
import com.attt.vazitaapp.ui.common.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DefectEditFragment : BaseFragment() {

    private var _binding: FragmentDefectEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DefectReviewViewModel
    private val args: DefectEditFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDefectEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        loadDefectForEdit()
        setupChapterSelection()
        setupPointSelection()
        setupAlterationSelection()
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

                // Update selected defect components
                viewModel.setSelectedChapter(defect.chapitre)
                viewModel.setSelectedPoint(defect.point)
                viewModel.setSelectedAlteration(defect.alteration)

                // Display current selections
                updateChapterSelection()
                updatePointSelection()
                updateAlterationSelection()
            }
        }

        viewModel.chapters.observe(viewLifecycleOwner) { chapters ->
            // Update UI when chapters are loaded
        }

        viewModel.points.observe(viewLifecycleOwner) { points ->
            // Update UI when points are loaded for selected chapter
        }

        viewModel.alterations.observe(viewLifecycleOwner) { alterations ->
            // Update UI when alterations are loaded for selected point
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { success ->
            binding.progressBar.visibility = View.GONE

            if (success) {
                Toast.makeText(context, R.string.defect_updated_successfully, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, R.string.defect_update_failed, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.progressBar.visibility = View.GONE
                showErrorView(it) {
                    loadDefectForEdit()
                }
            }
        }
    }

    private fun loadDefectForEdit() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE

        viewModel.loadDefectDetail(args.dossierNumber)
        viewModel.loadChapters()
    }

    private fun setupChapterSelection() {
        binding.btnSelectChapter.setOnClickListener {
            val chapters = viewModel.chapters.value ?: return@setOnClickListener

            val chapterNames = chapters.map { it.libelleChapter }.toTypedArray()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.select_chapter)
                .setItems(chapterNames) { _, which ->
                    val selectedChapter = chapters[which]
                    viewModel.setSelectedChapter(selectedChapter)
                    updateChapterSelection()

                    // Load points for this chapter
                    viewModel.loadPointsForChapter(selectedChapter.codeChapter)

                    // Reset point and alteration selections
                    viewModel.setSelectedPoint(null)
                    viewModel.setSelectedAlteration(null)
                    updatePointSelection()
                    updateAlterationSelection()
                }
                .show()
        }
    }

    private fun setupPointSelection() {
        binding.btnSelectPoint.setOnClickListener {
            val points = viewModel.points.value ?: return@setOnClickListener

            val pointNames = points.map { it.libellePoint }.toTypedArray()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.select_point)
                .setItems(pointNames) { _, which ->
                    val selectedPoint = points[which]
                    viewModel.setSelectedPoint(selectedPoint)
                    updatePointSelection()

                    // Load alterations for this point
                    val chapter = viewModel.getSelectedChapter()?.codeChapter ?: return@setItems
                    viewModel.loadAlterationsForPoint(chapter, selectedPoint.codePoint)

                    // Reset alteration selection
                    viewModel.setSelectedAlteration(null)
                    updateAlterationSelection()
                }
                .show()
        }
    }

    private fun setupAlterationSelection() {
        binding.btnSelectAlteration.setOnClickListener {
            val alterations = viewModel.alterations.value ?: return@setOnClickListener

            val alterationNames = alterations.map { it.libelleAlteration }.toTypedArray()

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.select_alteration)
                .setItems(alterationNames) { _, which ->
                    val selectedAlteration = alterations[which]
                    viewModel.setSelectedAlteration(selectedAlteration)
                    updateAlterationSelection()
                }
                .show()
        }
    }

    private fun updateChapterSelection() {
        val chapter = viewModel.getSelectedChapter()
        binding.tvSelectedChapter.text = chapter?.libelleChapter ?: getString(R.string.no_chapter_selected)
    }

    private fun updatePointSelection() {
        val point = viewModel.getSelectedPoint()
        binding.tvSelectedPoint.text = point?.libellePoint ?: getString(R.string.no_point_selected)
    }

    private fun updateAlterationSelection() {
        val alteration = viewModel.getSelectedAlteration()
        binding.tvSelectedAlteration.text = alteration?.libelleAlteration ?: getString(R.string.no_alteration_selected)
    }

    private fun validateAndSave() {
        val chapter = viewModel.getSelectedChapter()
        val point = viewModel.getSelectedPoint()
        val alteration = viewModel.getSelectedAlteration()

        if (chapter == null || point == null || alteration == null) {
            Toast.makeText(context, R.string.complete_all_fields, Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        // Create code defaut from chapter, point and alteration
        val codeDefaut = "${chapter.codeChapter}${point.codePoint}${alteration.codeAlteration}".toInt()

        viewModel.updateDefect(args.dossierNumber, codeDefaut)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_defect_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                validateAndSave()
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