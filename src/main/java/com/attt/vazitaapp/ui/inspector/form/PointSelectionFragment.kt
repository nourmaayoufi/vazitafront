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
import com.attt.vazitaapp.databinding.FragmentPointSelectionBinding
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.ui.inspector.form.adapter.PointAdapter
import com.attt.vazitaapp.ui.inspector.form.viewmodel.DefectFormViewModel

class PointSelectionFragment : BaseFragment() {

    private var _binding: FragmentPointSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DefectFormViewModel by activityViewModels()
    private lateinit var adapter: PointAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPointSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = PointAdapter { point ->
            viewModel.selectPoint(point)
            findNavController().navigate(R.id.action_pointSelectionFragment_to_alterationSelectionFragment)
        }

        binding.recyclerViewPoints.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = this@PointSelectionFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.points.observe(viewLifecycleOwner) { points ->
            adapter.submitList(points)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.selectedChapter.observe(viewLifecycleOwner) { chapter ->
            binding.textViewSelectedChapter.text = chapter.libelleChapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}