package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventRepository() {

    suspend fun getEventsR() = RetroFitInstance.api.getEvents()

    //Get events by date
    private val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    suspend fun getEventsByDateR() = RetroFitInstance.api.getEventsByDate(currentDate)

}