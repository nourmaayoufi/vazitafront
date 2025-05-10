package com.attt.vazitaapp.ui.admin.analytics.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.attt.vazitaapp.data.model.AnalyticsData
import com.attt.vazitaapp.data.repository.AnalyticsRepository
import com.attt.vazitaapp.ui.common.BaseViewModel
import kotlinx.coroutines.launch
import java.util.Date

class AnalyticsViewModel(private val analyticsRepository: AnalyticsRepository) : BaseViewModel() {

    private val _analyticsData = MutableLiveData<AnalyticsData>()
    val analyticsData: LiveData<AnalyticsData> = _analyticsData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentCarType: String? = null
    private var currentStartDate: Date? = null
    private var currentEndDate: Date? = null
    private var currentCenterId: Int? = null

    init {
        // Load default analytics data on initialization
        loadAnalyticsData()
    }

    fun loadAnalyticsData(carType: String? = null, startDate: Date? = null, endDate: Date? = null, centerId: Int? = null) {
        _isLoading.value = true

        // Update filter values
        this.currentCarType = carType ?: this.currentCarType
        this.currentStartDate = startDate ?: this.currentStartDate
        this.currentEndDate = endDate ?: this.currentEndDate
        this.currentCenterId = centerId ?: this.currentCenterId

        viewModelScope.launch {
            try {
                val result = analyticsRepository.getAnalyticsData(
                    carType = this@AnalyticsViewModel.currentCarType,
                    startDate = this@AnalyticsViewModel.currentStartDate,
                    endDate = this@AnalyticsViewModel.currentEndDate,
                    centerId = this@AnalyticsViewModel.currentCenterId
                )
                _analyticsData.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load analytics data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        loadAnalyticsData()
    }

    fun exportAnalyticsData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                analyticsRepository.exportAnalyticsData(
                    carType = currentCarType,
                    startDate = currentStartDate,
                    endDate = currentEndDate,
                    centerId = currentCenterId
                )
                _success.value = "Analytics data exported successfully"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to export analytics data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetFilters() {
        currentCarType = null
        currentStartDate = null
        currentEndDate = null
        currentCenterId = null
        loadAnalyticsData()
    }
}