package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance

class EventRepository() {

    suspend fun getEventsR() = RetroFitInstance.api.getEvents()

}