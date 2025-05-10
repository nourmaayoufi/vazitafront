package com.attt.vazitaapp.ui.admin.users.viewmodel



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.data.repository.UserRepository
import com.attt.vazitaapp.ui.common.BaseViewModel
import kotlinx.coroutines.launch
import java.io.IOException

class UserManagementViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _selectedUser = MutableLiveData<User>()
    val selectedUser: LiveData<User> = _selectedUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _operationSuccessful = MutableLiveData<Boolean>()
    val operationSuccessful: LiveData<Boolean> = _operationSuccessful

    fun loadUsers() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.getUsers()
                _users.value = result
                _isLoading.value = false
            } catch (e: IOException) {
                _errorMessage.value = "Network error: ${e.message}"
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    fun createUser(user: User) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.createUser(user)
                _operationSuccessful.value = true
                loadUsers()
            } catch (e: IOException) {
                _errorMessage.value = "Network error: ${e.message}"
                _operationSuccessful.value = false
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _operationSuccessful.value = false
                _isLoading.value = false
            }
        }
    }

    fun updateUser(user: User) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.updateUser(user)
                _operationSuccessful.value = true
                loadUsers()
            } catch (e: IOException) {
                _errorMessage.value = "Network error: ${e.message}"
                _operationSuccessful.value = false
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _operationSuccessful.value = false
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.deleteUser(userId)
                _operationSuccessful.value = true
                loadUsers()
            } catch (e: IOException) {
                _errorMessage.value = "Network error: ${e.message}"
                _operationSuccessful.value = false
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _operationSuccessful.value = false
                _isLoading.value = false
            }
        }
    }

    fun resetOperationStatus() {
        _operationSuccessful.value = false
        _errorMessage.value = null
    }
}