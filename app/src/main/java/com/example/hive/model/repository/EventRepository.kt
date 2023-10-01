package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance
import com.example.hive.model.network.requests.CreateEventRequest

class EventRepository() {

    suspend fun getEventsR() = RetroFitInstance.api.getEvents()
    suspend fun getEventsByIdR(id: String) = RetroFitInstance.api.getEventsById(id)
    suspend fun postEventrR(createEventRequest: CreateEventRequest) = RetroFitInstance.api.createEvent(createEventRequest)
}