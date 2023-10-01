package com.example.hive.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.example.hive.model.repository.EventRepository
import androidx.lifecycle.ViewModel
import com.example.hive.model.models.UserSession

class EventsViewModelProviderFactory(private val eventRepository: EventRepository, private val userSession: UserSession) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventListViewModel(eventRepository, userSession) as T
    }
}