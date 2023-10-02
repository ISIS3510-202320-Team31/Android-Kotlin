package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventRepository() {

    //Get current date as yyyy-mm-dd
    private val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    suspend fun getEventsR() = RetroFitInstance.api.getEvents()
    suspend fun getEventsByIdR(id: String) = RetroFitInstance.api.getEventsById(id)
    suspend fun getEventsByDateAndUserR(user_id: String, future: String) = RetroFitInstance.api.getEventsByDateAndUser(currentDate, user_id, future)
    suspend fun addParticipatEventR(userId: String, eventId: String) = RetroFitInstance.api.addParticipatEvent(userId, eventId)
    suspend fun deleteParticipatEventR(userId: String, eventId: String) = RetroFitInstance.api.deleteParticipatEvent(userId, eventId)
    suspend fun getEventsByDateR(date: String) = RetroFitInstance.api.getEventsByDate(date)
    suspend fun getSmartFeatureR(id: String) = RetroFitInstance.api.getSmartFeature(id)

}