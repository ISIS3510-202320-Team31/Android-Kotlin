package com.example.hive.model.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.example.hive.model.di.RetroFitInstance
import com.example.hive.model.network.requests.LoginRequest
import com.example.hive.model.network.requests.RegisterRequest
import com.example.hive.model.room.HiveDatabase
import com.example.hive.model.room.entities.CategoryChart
import com.example.hive.model.room.entities.TopPartners
import com.example.hive.model.room.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(context: Context) {

    private val hiveDatabase = HiveDatabase.getInstance(context)
    private val userDao = hiveDatabase?.userDao()
    private val categoryDao = hiveDatabase?.categoryChartDao()
    private val topPartnersDao = hiveDatabase?.topPartnersDao()

    //Room
    val allUsers: Flow<List<User>>? = userDao?.getAll()
    val allCategories: Flow<List<CategoryChart>>? = categoryDao?.getAll()
    val allTopPartners: Flow<List<TopPartners>>? = topPartnersDao?.getAll()

    //USER
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertUser(user: User) = userDao?.insert(user)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun  deleteAllUsers() = userDao?.deleteAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findUserById(id: String): Flow<User>? = userDao?.findById(id)

    //CATEGORY
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCategory(category: CategoryChart) = categoryDao?.insert(category)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllCategories() = categoryDao?.deleteAll()

    //TOP PARTNERS
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTopPartner(topPartner: TopPartners) = topPartnersDao?.insert(topPartner)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllTopPartners() = topPartnersDao?.deleteAll()

    //User
    suspend fun registerR(registerRequest: RegisterRequest) = RetroFitInstance.api.registerUser(registerRequest)
    suspend fun loginR(loginRequest: LoginRequest) = RetroFitInstance.api.loginUser(loginRequest)
    suspend fun getParticipationR(user_id: String) = RetroFitInstance.api.getParticipation(user_id)
    suspend fun getUserByIdR(user_id: String) = RetroFitInstance.api.getUserById(user_id)

    //Category
    suspend fun getCategoriesR(user_id: String) = RetroFitInstance.api.getCategories(user_id)

    //Top Partners
    suspend fun getTopPartnersR(user_id: String) = RetroFitInstance.api.getTopPartners(user_id)
}