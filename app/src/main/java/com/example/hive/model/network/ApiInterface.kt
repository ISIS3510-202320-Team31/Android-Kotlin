package com.example.hive.model.network

import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.network.responses.EventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    // Get all events
    @GET("/events/")
    suspend fun getEvents(): Response<List<EventResponse>>

    // Get events by date
    @GET("/events/date/{date}/")
    suspend fun getEventsByDate(@Path("date") date:  String): Response<List<EventResponse>>

    //Get detail of event
    @GET("/events/{id}")
    suspend fun getEventsById(@Path("id") id: String): Response<EventDetailResponse>

    //Get events by date and user
    @GET("/events/date/{date}/user/{user_id}/order/{future}")
    suspend fun getEventsByDateAndUser(@Path("date") date:  String, @Path("user_id") user_id: String, @Path("future") future: String): Response<List<EventResponse>>
}