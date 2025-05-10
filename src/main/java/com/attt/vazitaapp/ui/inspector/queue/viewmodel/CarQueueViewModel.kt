package com.attt.vazitaapp.ui.inspector.queue.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attt.vazitaapp.data.model.CarQueue
import com.attt.vazitaapp.data.repository.InspectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarQueueViewModel @Inject constructor(
    private val inspectionRepository: InspectionRepository
) : ViewModel() {

    private val _carQueueList = MutableLiveData<List<CarQueue>>()
    val carQueueList: LiveData<List<CarQueue>> = _carQueueList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadCarQueue() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = inspectionRepository.getCarQueue()
                _carQueueList.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                _carQueueList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshQueue() {
        loadCarQueue()
    }
}