package com.example.hive.model.network

import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.network.responses.EventResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {

    @GET("/events/")
    suspend fun getEvents(): Response<List<EventResponse>>

    @GET("/events/{id}")
    suspend fun getEventsById(@Path("id") id: String): Response<EventDetailResponse>

    @POST("/users/{userId}/events/{eventId}/")
    suspend fun addParticipatEvent(@Path("userId") userId: String, @Path("eventId") eventId: String): Response<EventDetailResponse>

    @DELETE("/users/{userId}/events/{eventId}/")
    suspend fun deleteParticipatEvent(@Path("userId") userId: String, @Path("eventId") eventId: String): Response<EventDetailResponse>

}