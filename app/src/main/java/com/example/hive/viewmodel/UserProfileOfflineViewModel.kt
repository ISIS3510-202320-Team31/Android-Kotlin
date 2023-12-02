package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.hive.model.repository.UserRepository
import com.example.hive.model.room.daos.TopPartnersDao
import com.example.hive.model.room.entities.CategoryChart
import com.example.hive.model.room.entities.TopPartners
import com.example.hive.model.room.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileOfflineViewModel(private val context: Context): ViewModel() {

    val repository = UserRepository(context)
    val allUsers: LiveData<List<User>>? = repository.allUsers?.asLiveData()
    val allCategories: LiveData<List<CategoryChart>>? = repository.allCategories?.asLiveData()
    val allTopPartners: LiveData<List<TopPartners>>? = repository.allTopPartners?.asLiveData()

    //CATEGORY
    fun insertCategory(category: CategoryChart) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(category)
    }

    fun removeCategoryDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllCategories()
    }


    //USER
    fun removeUserDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllUsers()
    }

    fun insertOneToDatabase(user: User) = viewModelScope.launch(Dispatchers.IO) {
        println("Inserting user to database")
        println(user)
        repository.insertUser(user)
    }

    //TOP PARTNERS
    fun insertTopPartner(topPartner: TopPartners) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTopPartner(topPartner)
    }

    fun removeTopPartnersDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllTopPartners()
    }


}