package com.attt.vazitaapp.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val viewModelJob = Job()
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    protected fun launchDataLoad(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                withContext(Dispatchers.IO) {
                    block()
                }
            } catch (error: Exception) {
                _errorMessage.postValue(error.message ?: "An unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}