package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.adapters.SessionManager

class UserProfileOfflineViewModelProviderFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserProfileOfflineViewModel(context) as T
    }
}