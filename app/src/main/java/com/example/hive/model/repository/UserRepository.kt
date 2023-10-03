package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance
import com.example.hive.model.network.requests.LoginRequest
import com.example.hive.model.network.requests.RegisterRequest

class UserRepository {

    suspend fun registerR(registerRequest: RegisterRequest) = RetroFitInstance.api.registerUser(registerRequest)
    suspend fun loginR(loginRequest: LoginRequest) = RetroFitInstance.api.loginUser(loginRequest)
    suspend fun getParticipationR(user_id: String) = RetroFitInstance.api.getParticipation(user_id)
    suspend fun getUserByIdR(user_id: String) = RetroFitInstance.api.getUserById(user_id)

}