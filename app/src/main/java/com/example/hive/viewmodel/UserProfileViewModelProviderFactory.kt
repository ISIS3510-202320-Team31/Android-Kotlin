package com.example.hive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.repository.UserRepository

class UserProfileViewModelProviderFactory(private val repository: UserRepository, private val sessionManager: SessionManager): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserProfileViewModel(repository, sessionManager) as T
    }
}