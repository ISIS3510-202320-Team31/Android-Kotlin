package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.models.UserSession

class TopCreatorsViewModelProviderFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TopCreatorsViewModel(context) as T
    }
}