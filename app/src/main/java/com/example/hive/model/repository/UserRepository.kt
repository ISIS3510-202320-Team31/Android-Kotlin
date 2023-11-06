package com.example.hive.model.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.example.hive.model.di.RetroFitInstance
import com.example.hive.model.network.requests.LoginRequest
import com.example.hive.model.network.requests.RegisterRequest
import com.example.hive.model.room.HiveDatabase
import com.example.hive.model.room.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(context: Context) {

    private val hiveDatabase = HiveDatabase.getInstance(context)
    private val userDao = hiveDatabase?.userDao()

    //Room
    val allUsers: Flow<List<User>>? = userDao?.getAll()

    //USER
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUser(user: User) = userDao?.insert(user)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun  deleteAllUsers() = userDao?.deleteAll()


    suspend fun registerR(registerRequest: RegisterRequest) = RetroFitInstance.api.registerUser(registerRequest)
    suspend fun loginR(loginRequest: LoginRequest) = RetroFitInstance.api.loginUser(loginRequest)
    suspend fun getParticipationR(user_id: String) = RetroFitInstance.api.getParticipation(user_id)
    suspend fun getUserByIdR(user_id: String) = RetroFitInstance.api.getUserById(user_id)

}