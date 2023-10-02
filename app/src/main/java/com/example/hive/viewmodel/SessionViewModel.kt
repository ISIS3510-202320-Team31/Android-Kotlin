package com.example.hive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.models.UserSession

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _userSessionLiveData = MutableLiveData<UserSession>()
    val userSessionLiveData: LiveData<UserSession>
        get() = _userSessionLiveData

    init {
        // Initialize the ViewModel with the user session data from SharedPreferences
        _userSessionLiveData.value = sessionManager.getUserSession()
    }

    fun saveUserSession(userSession: UserSession) {
        _userSessionLiveData.value = userSession
        sessionManager.saveUserSession(userSession)
    }

    fun clearSession() {
        _userSessionLiveData.value = UserSession()
        sessionManager.clearSession()
    }
}