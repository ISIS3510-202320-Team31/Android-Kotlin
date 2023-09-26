package com.example.hive.model.network

import com.example.hive.model.network.responses.EventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/events/")
    suspend fun getEvents(): Response<List<EventResponse>>
}