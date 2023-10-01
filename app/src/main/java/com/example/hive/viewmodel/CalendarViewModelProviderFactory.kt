package com.example.hive.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.repository.EventRepository
import androidx.lifecycle.ViewModel

class CalendarViewModelProviderFactory(private val eventRepository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalendarListViewModel(eventRepository) as T
    }
}