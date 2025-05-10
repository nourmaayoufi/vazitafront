package com.attt.vazitaapp.ui.inspector.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.attt.vazitaapp.R
import com.attt.vazitaapp.data.model.CarQueue
import com.attt.vazitaapp.ui.common.BaseFragment
import com.attt.vazitaapp.ui.common.component.ErrorView

class CarQueueFragment : BaseFragment() {

    private val viewModel: CarQueueViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var errorView: ErrorView
    private lateinit var emptyView: View
    private lateinit var adapter: CarQueueAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_car_queue, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupListeners()

        // Load car queue data
        viewModel.loadCarQueue()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        errorView = view.findViewById(R.id.errorView)
        emptyView = view.findViewById(R.id.emptyView)
    }

    private fun setupRecyclerView() {
        adapter = CarQueueAdapter { carQueue ->
            navigateToDefectForm(carQueue)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = this@CarQueueFragment.adapter
        }
    }

    private fun setupListeners() {
        swipeRefresh.setOnRefreshListener {
            viewModel.loadCarQueue()
        }

        errorView.setOnRetryClickListener {
            viewModel.loadCarQueue()
        }
    }

    private fun navigateToDefectForm(carQueue: CarQueue) {
        val action = CarQueueFragmentDirections.actionCarQueueFragmentToDefectFormActivity(
            dossierNumber = carQueue.dossierNumber,
            chassisNumber = carQueue.chassisNumber,
            registrationNumber = carQueue.registrationNumber
        )
        findNavController().navigate(action)
    }

    override fun setupObservers() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            showError(error)
            updateViewVisibility(hasError = true)
            errorView.setErrorMessage(error)
        })

        viewModel.carQueue.observe(viewLifecycleOwner, Observer { carQueueList ->
            adapter.submitList(carQueueList)
            updateViewVisibility(isEmpty = carQueueList.isEmpty())
        })
    }

    private fun updateViewVisibility(hasError: Boolean = false, isEmpty: Boolean = false) {
        recyclerView.visibility = if (!hasError && !isEmpty) View.VISIBLE else View.GONE
        errorView.visibility = if (hasError) View.VISIBLE else View.GONE
        emptyView.visibility = if (!hasError && isEmpty) View.VISIBLE else View.GONE
    }
}