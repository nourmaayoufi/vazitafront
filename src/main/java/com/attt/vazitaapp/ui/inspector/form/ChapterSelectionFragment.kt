package com.attt.vazitaapp.ui.inspector.form


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.attt.vazitaapp.R
import com.attt.vazitaapp.databinding.FragmentChapterSelectionBinding
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.ui.inspector.form.adapter.ChapterAdapter
import com.attt.vazitaapp.ui.inspector.form.viewmodel.DefectFormViewModel

class ChapterSelectionFragment : BaseFragment() {

    private var _binding: FragmentChapterSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DefectFormViewModel by activityViewModels()
    private lateinit var adapter: ChapterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChapterSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Reset selections when starting a new form
        viewModel.resetSelections()
    }

    private fun setupRecyclerView() {
        adapter = ChapterAdapter { chapter ->
            viewModel.selectChapter(chapter)
            findNavController().navigate(R.id.action_chapterSelectionFragment_to_pointSelectionFragment)
        }

        binding.recyclerViewChapters.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = this@ChapterSelectionFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.chapters.observe(viewLifecycleOwner) { chapters ->
            adapter.submitList(chapters)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.carDossier.observe(viewLifecycleOwner) { carDossier ->
            binding.textViewCarInfo.text = getString(
                R.string.car_info_format,
                carDossier.immatriculation,
                carDossier.numChassis
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
