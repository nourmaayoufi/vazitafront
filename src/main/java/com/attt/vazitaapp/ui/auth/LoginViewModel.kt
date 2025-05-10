package com.attt.vazitaapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.data.repository.AuthRepository
import com.attt.vazitaapp.ui.common.BaseViewModel

class LoginViewModel : BaseViewModel() {

    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<User>()
    val loginResult: LiveData<User> = _loginResult

    fun login(username: String, password: String) {
        launchWithLoading {
            val user = authRepository.login(username, password)
            _loginResult.postValue(user)
        }
    }
}