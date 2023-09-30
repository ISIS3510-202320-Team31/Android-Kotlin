package com.example.hive.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.repository.EventRepository
import androidx.lifecycle.ViewModel

class CalendarViewModelProviderFactory(private val eventRepository: EventRepository, private val user_id: String, private val future: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalendarListViewModel(eventRepository, user_id, future) as T
    }
}