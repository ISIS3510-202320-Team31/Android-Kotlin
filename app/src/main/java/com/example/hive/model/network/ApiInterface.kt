package com.example.hive.model.network

import com.example.hive.model.network.requests.RegisterRequest
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.network.responses.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {

    @GET("/events/")
    suspend fun getEvents(): Response<List<EventResponse>>

    @GET("/events/{id}")
    suspend fun getEventsById(@Path("id") id: String): Response<EventDetailResponse>

    @POST("/register/")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<UserResponse>
}