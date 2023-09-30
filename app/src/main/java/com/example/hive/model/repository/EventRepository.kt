package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventRepository() {

    suspend fun getEventsR() = RetroFitInstance.api.getEvents()
    suspend fun getEventsByIdR(id: String) = RetroFitInstance.api.getEventsById(id)

    //Get current date
    private val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    //Get events by date
    suspend fun getEventsByDateR() = RetroFitInstance.api.getEventsByDate(currentDate)

    //Get events by date and user
    suspend fun getEventsByDateAndUserR(user_id: String, future: String) = RetroFitInstance.api.getEventsByDateAndUser(currentDate, user_id, future)



}