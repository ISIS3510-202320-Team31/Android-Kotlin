package com.example.hive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.repository.EventRepository

class AddParticipatEventViewModelProviderFactory (private val eventRepository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddParticipatEventViewModel(eventRepository) as T
    }

}