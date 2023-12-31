package com.example.hive.model.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.example.hive.model.di.RetroFitInstance
import com.example.hive.model.network.requests.CreateEventRequest
import com.example.hive.model.network.requests.EditEventRequest
import com.example.hive.model.room.HiveDatabase
import com.example.hive.model.room.entities.Event
import com.example.hive.model.room.entities.EventActivities
import com.example.hive.model.room.entities.EventHistorical
import com.example.hive.model.room.entities.EventUser
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventRepository(context: Context) {

    private val hiveDatabase = HiveDatabase.getInstance(context)
    private val eventDao = hiveDatabase?.eventDao()
    private val eventActivitiesDao = hiveDatabase?.eventActivitiesDao()
    private val eventHistoricalDao = hiveDatabase?.eventHistoricalDao()
    private val eventUserDao = hiveDatabase?.eventUserDao()

    //Get current date as yyyy-mm-dd
    private val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    //Room
    val allEvents: Flow<List<Event>>? = eventDao?.getAll()
    val allEventActivities: Flow<List<EventActivities>>? = eventActivitiesDao?.getAll()
    val allEventHistorical: Flow<List<EventHistorical>>? = eventHistoricalDao?.getAll()
    val allEventUser: Flow<List<EventUser>>? = eventUserDao?.getAll()

    //EVENTS
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(vararg events: Event) = eventDao?.insertAll(*events)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun  deleteAll() = eventDao?.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(event: Event) = eventDao?.insert(event)

    //EVENTS ACTIVITIES
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllActivities(vararg eventsActivities: EventActivities) = eventActivitiesDao?.insertAll(*eventsActivities)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun  deleteAllActivities() = eventActivitiesDao?.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertActivities(eventActivities: EventActivities) = eventActivitiesDao?.insert(eventActivities)

    //EVENTS HISTORICAL
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllHistorical(vararg eventsHistorical: EventHistorical) = eventHistoricalDao?.insertAll(*eventsHistorical)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun  deleteAllHistorical() = eventHistoricalDao?.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertHistorical(eventHistorical: EventHistorical) = eventHistoricalDao?.insert(eventHistorical)

    //EVENTS CREATED BY THE USER
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllUser(vararg eventsUser: EventUser) = eventUserDao?.insertAll(*eventsUser)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun  deleteAllUser() = eventUserDao?.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUser(eventUser: EventUser) = eventUserDao?.insert(eventUser)


    //Retrofit
    suspend fun getEventsR() = RetroFitInstance.api.getEvents()

    //Manage connectivity issues
    suspend fun getEventsByIdR(id: String) = RetroFitInstance.api.getEventsById(id)
    suspend fun createEventrR(createEventRequest: CreateEventRequest) = RetroFitInstance.api.createEvent(createEventRequest)
    suspend fun editEventrR(id: String, editEventRequest: EditEventRequest) = RetroFitInstance.api.editEvent(id, editEventRequest)
    suspend fun getEventsByDateAndUserR(user_id: String, future: String) = RetroFitInstance.api.getEventsByDateAndUser(currentDate, user_id, future)
    suspend fun addParticipatEventR(userId: String, eventId: String) = RetroFitInstance.api.addParticipatEvent(userId, eventId)
    suspend fun deleteParticipatEventR(userId: String, eventId: String) = RetroFitInstance.api.deleteParticipatEvent(userId, eventId)
    suspend fun getEventsByDateR(date: String) = RetroFitInstance.api.getEventsByDate(date)
    suspend fun getSmartFeatureR(id: String) = RetroFitInstance.api.getSmartFeature(id)
    suspend fun getEventsCreatedByR(id: String) = RetroFitInstance.api.getEventsCreatedBy(id)

}