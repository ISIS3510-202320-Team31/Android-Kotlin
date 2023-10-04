package com.example.hive.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class EventDetailViewModel(private val repository: EventRepository, private val id: String) : ViewModel() {
    val eventById: MutableLiveData<Resource<EventDetailResponse>> = MutableLiveData()

    init {
        getEventByIdVM(id)
    }

    fun getEventByIdVM(id: String) = viewModelScope.launch {
        eventById.postValue(Resource.Loading())
        val response = repository.getEventsByIdR(id)
        eventById.postValue(handleResponse(response))
    }


    private fun handleResponse(response: Response<EventDetailResponse>): Resource<EventDetailResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}