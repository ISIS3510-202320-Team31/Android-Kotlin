package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.hive.model.repository.UserRepository
import com.example.hive.model.room.entities.*
import kotlinx.coroutines.launch


class TopCreatorsOfflineViewModel(private val context: Context) : ViewModel() {

    val repository = UserRepository(context)
    val allTopCreators: LiveData<List<TopCreators>>? = repository.allTopCreators?.asLiveData()

    fun insertToDatabase(topCreators: List<TopCreators>) = viewModelScope.launch {
        repository.insertTopCreators(*topCreators.toTypedArray())
    }

    fun removeTopCreatorsDatabase() = viewModelScope.launch {
        repository.deleteAllTopCreators()
    }

}