package com.example.hive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.repository.EventRepository

class EventCreationViewModelProviderFactory (private val repository: EventRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventCreationViewModel(repository) as T
    }
}