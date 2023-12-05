package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.hive.model.repository.EventRepository
import com.example.hive.model.room.entities.Event
import com.example.hive.model.room.entities.EventActivities
import com.example.hive.model.room.entities.EventHistorical
import com.example.hive.model.room.entities.EventUser
import kotlinx.coroutines.launch

class EventListOfflineViewModel(private val context: Context) : ViewModel() {

    val repository = EventRepository(context)
    val allEvents: LiveData<List<Event>>? = repository.allEvents?.asLiveData()
    val allEventActivities: LiveData<List<EventActivities>>? = repository.allEventActivities?.asLiveData()
    val allEventHistorical: LiveData<List<EventHistorical>>? = repository.allEventHistorical?.asLiveData()
    val allEventCreados: LiveData<List<EventUser>>? = repository.allEventUser?.asLiveData()

    // EVENTS
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

    //EVENTS ACTIVITIES
    fun insertToDatabaseActivities(eventsActivities: List<EventActivities>) = viewModelScope.launch {
        repository.insertAllActivities(*eventsActivities.toTypedArray())
    }

    fun removeEventActivitiesDatabase() = viewModelScope.launch {
        repository.deleteAllActivities()
    }

    fun insertOneToDatabaseActivities(eventActivities: EventActivities) = viewModelScope.launch {
        println("Inserting event activities to database")
        println(eventActivities)
        repository.insertActivities(eventActivities)
    }

    //EVENTS HISTORICAL
    fun insertToDatabaseHistorical(eventsHistorical: List<EventHistorical>) = viewModelScope.launch {
        repository.insertAllHistorical(*eventsHistorical.toTypedArray())
    }

    fun removeEventHistoricalDatabase() = viewModelScope.launch {
        repository.deleteAllHistorical()
    }

    fun insertOneToDatabaseHistorical(eventHistorical: EventHistorical) = viewModelScope.launch {
        println("Inserting event historical to database")
        println(eventHistorical)
        repository.insertHistorical(eventHistorical)
    }

    //EVENTS CREADOS
    fun insertToDatabaseCreados(eventsCreados: List<EventUser>) = viewModelScope.launch {
        repository.insertAllUser(*eventsCreados.toTypedArray())
    }

    fun removeEventCreadosDatabase() = viewModelScope.launch {
        repository.deleteAllUser()
    }

    fun insertOneToDatabaseCreados(eventCreados: EventUser) = viewModelScope.launch {
        println("Inserting event creados to database")
        println(eventCreados)
        repository.insertUser(eventCreados)
    }

}


