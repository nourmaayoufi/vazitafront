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
import com.attt.vazitaapp.databinding.FragmentAlterationSelectionBinding
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.ui.inspector.form.adapter.AlterationAdapter
import com.attt.vazitaapp.ui.inspector.form.viewmodel.DefectFormViewModel

class AlterationSelectionFragment : BaseFragment() {

    private var _binding: FragmentAlterationSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DefectFormViewModel by activityViewModels()
    private lateinit var adapter: AlterationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlterationSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupButtons()
    }

    private fun setupRecyclerView() {
        adapter = AlterationAdapter(
            onAlterationSelected = { alteration ->
                viewModel.toggleAlteration(alteration)
                adapter.notifyDataSetChanged()
            },
            isSelected = { alteration ->
                viewModel.isAlterationSelected(alteration)
            }
        )

        binding.recyclerViewAlterations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = this@AlterationSelectionFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.alterations.observe(viewLifecycleOwner) { alterations ->
            adapter.submitList(alterations)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.selectedChapter.observe(viewLifecycleOwner) { chapter ->
            binding.textViewSelectedChapter.text = chapter.libelleChapter
        }

        viewModel.selectedPoint.observe(viewLifecycleOwner) { point ->
            binding.textViewSelectedPoint.text = point.libellePoint
        }
    }

    private fun setupButtons() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.action_alterationSelectionFragment_to_formSummaryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
