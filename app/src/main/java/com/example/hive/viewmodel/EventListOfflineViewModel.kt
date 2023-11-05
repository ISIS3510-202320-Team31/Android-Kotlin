package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.model.room.entities.Event
import kotlinx.coroutines.launch

class EventListOfflineViewModel(private val context: Context) : ViewModel() {

    val repository = EventRepository(context)
    val allEvents: LiveData<List<Event>>? = repository.allEvents?.asLiveData()

    fun insertToDatabase(events: List<Event>) = viewModelScope.launch {
        repository.insertAll(*events.toTypedArray())
    }

    fun removeEventDatabase() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun insertOneToDatabase(event: Event) = viewModelScope.launch {
        println("Inserting event to database")
        println(event)
        repository.insert(event)
    }

}


