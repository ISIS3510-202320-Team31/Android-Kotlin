package com.example.hive.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class AddParticipatEventViewModel() : ViewModel()  {
    val addParticipatEvent: MutableLiveData<Resource<EventDetailResponse>> = MutableLiveData()
    val deleteParticipatEvent: MutableLiveData<Resource<EventDetailResponse>> = MutableLiveData()
    val repository = EventRepository()

    fun addParticipatEventVM(eventID: String, userID: String) = viewModelScope.launch {
        addParticipatEvent.postValue(Resource.Loading())
        val peticion = repository.addParticipatEventR(userID, eventID)
        addParticipatEvent.postValue(handleAddParticipatEventResponse(peticion))
    }

    fun deleteParticipatEventVM(eventID: String, userID: String) = viewModelScope.launch {
        deleteParticipatEvent.postValue(Resource.Loading())
        val peticion = repository.deleteParticipatEventR(userID, eventID)
        deleteParticipatEvent.postValue(handleAddParticipatEventResponse(peticion))
    }

    private fun handleAddParticipatEventResponse(response: Response<EventDetailResponse>): Resource<EventDetailResponse>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}