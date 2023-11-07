package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.example.hive.model.repository.UserRepository
import com.example.hive.model.room.entities.User
import kotlinx.coroutines.launch

class UserProfileOfflineViewModel(private val context: Context): ViewModel() {

    val repository = UserRepository(context)
    val allUsers: LiveData<List<User>>? = repository.allUsers?.asLiveData()

    suspend fun getUserById(id: String): LiveData<User>? = repository.findUserById(id)?.asLiveData()


    fun removeUserDatabase() = viewModelScope.launch {
        repository.deleteAllUsers()
    }

    fun insertOneToDatabase(user: User) = viewModelScope.launch {
        println("Inserting user to database")
        println(user)
        repository.insertUser(user)
    }


}