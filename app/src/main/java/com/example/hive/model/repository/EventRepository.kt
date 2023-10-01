package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance

class EventRepository() {

    suspend fun getEventsR() = RetroFitInstance.api.getEvents()
    suspend fun getEventsByIdR(id: String) = RetroFitInstance.api.getEventsById(id)
    suspend fun getEventsByDateR(date: String) = RetroFitInstance.api.getEventsByDate(date)
    suspend fun getSmartFeatureR(id: String) = RetroFitInstance.api.getSmartFeature(id)

}