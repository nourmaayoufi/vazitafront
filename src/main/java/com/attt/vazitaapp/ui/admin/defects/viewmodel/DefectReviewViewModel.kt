package com.attt.vazitaapp.ui.admin.defects.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.attt.vazitaapp.data.model.DossierDefect
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.data.repository.DefectFormRepository
import com.attt.vazitaapp.data.repository.UserRepository
import com.attt.vazitaapp.ui.common.BaseViewModel
import kotlinx.coroutines.launch
import java.util.Date

class DefectReviewViewModel : BaseViewModel() {

    private val defectRepository = DefectFormRepository()
    private val userRepository = UserRepository()

    private val _defects = MutableLiveData<List<DossierDefect>>()
    val defects: LiveData<List<DossierDefect>> = _defects

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Filter parameters
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var inspectorId: String? = null

    fun loadDefects(query: String, page: Int, pageSize: Int) {
        viewModelScope.launch {
            try {
                val defectsList = defectRepository.getDefects(
                    query = query,
                    page = page,
                    pageSize = pageSize,
                    startDate = startDate,
                    endDate = endDate,
                    inspectorId = inspectorId
                )
                _defects.value = defectsList
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            }
        }
    }

    fun loadInspectors(): LiveData<List<User>> {
        val inspectorsLiveData = MutableLiveData<List<User>>()

        viewModelScope.launch {
            try {
                val inspectors = userRepository.getInspectorsByCenterId()
                inspectorsLiveData.postValue(inspectors)
            } catch (e: Exception) {
                // Handle error
                _error.value = e.message ?: "Failed to load inspectors"
            }
        }

        return inspectorsLiveData
    }

    fun setDateRange(start: Date, end: Date) {
        startDate = start
        endDate = end
    }

    fun clearDateRange() {
        startDate = null
        endDate = null
    }

    fun setInspector(id: String) {
        inspectorId = id
    }

    fun clearInspector() {
        inspectorId = null
    }

    fun clearFilters() {
        startDate = null
        endDate = null
        inspectorId = null
    }
}