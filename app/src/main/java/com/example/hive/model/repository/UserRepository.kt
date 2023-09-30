package com.example.hive.model.repository

import com.example.hive.model.di.RetroFitInstance
import com.example.hive.model.network.requests.RegisterRequest

class UserRepository {

    suspend fun registerR(registerRequest: RegisterRequest) = RetroFitInstance.api.registerUser(registerRequest)

}