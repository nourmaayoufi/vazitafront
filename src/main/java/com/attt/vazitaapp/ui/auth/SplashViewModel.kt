package com.attt.vazitaapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.attt.vazitaapp.data.model.User
import com.attt.vazitaapp.data.repository.AuthRepository
import com.attt.vazitaapp.ui.common.BaseViewModel

class SplashViewModel : BaseViewModel() {

    private val authRepository = AuthRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun checkLoggedInUser() {
        launchWithLoading {
            val loggedInUser = authRepository.getLoggedInUser()
            _user.postValue(loggedInUser)
        }
    }
}