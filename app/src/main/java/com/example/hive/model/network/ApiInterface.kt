package com.example.hive.model.network

import com.example.hive.model.network.requests.CreateEventRequest
import com.example.hive.model.network.requests.EditEventRequest
import com.example.hive.model.network.requests.LoginRequest
import com.example.hive.model.network.requests.RegisterRequest
import com.example.hive.model.network.responses.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @GET("/events/")
    suspend fun getEvents(): Response<List<EventResponse>>

    @GET("/events/{id}")
    suspend fun getEventsById(@Path("id") id: String): Response<EventDetailResponse>

    @GET("/events/date/{date}/user/{user_id}/order/{future}")
    suspend fun getEventsByDateAndUser(@Path("date") date: String, @Path("user_id") user_id: String, @Path("future") future: String): Response<List<EventResponse>>
    
    @POST("/users/{userId}/events/{eventId}/")
    suspend fun addParticipatEvent(@Path("userId") userId: String, @Path("eventId") eventId: String): Response<EventDetailResponse>

    @DELETE("/users/{userId}/events/{eventId}/")
    suspend fun deleteParticipatEvent(@Path("userId") userId: String, @Path("eventId") eventId: String): Response<EventDetailResponse>

    @POST("/register/")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<UserResponse>

    @POST("/login/")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<UserResponse>

    @POST("/events/")
    suspend fun createEvent(@Body createEventRequest: CreateEventRequest): Response<CreateEventResponse>

    @PUT("/events/edit/{id}/")
    suspend fun editEvent(@Path("id") id: String, @Body editEventRequest: EditEventRequest): Response<EditEventResponse>

    @POST("/events/date/{date}/")
    suspend fun getEventsByDate(@Path("date") date: String): Response<List<EventResponse>>
    
    @GET("/events/users/{id}/")
    suspend fun getSmartFeature(@Path("id") id: String): Response<List<EventResponse>>

    @GET("/events/users/{userId}/participation/")
    suspend fun getParticipation(@Path("userId") userId: String): Response<UserParticipationResponse>

    @GET("/users/{userId}/")
    suspend fun getUserById(@Path("userId") userId: String): Response<UserResponse>

    @GET("/events/users/{userId}/createdby/")
    suspend fun getEventsCreatedBy(@Path("userId") userId: String): Response<List<EventResponse>>

    @GET("/top_creators/")
    suspend fun getTopCreators(): Response<List<TopCreatorsResponse>>
}