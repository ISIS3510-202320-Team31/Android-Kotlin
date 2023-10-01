package com.example.hive.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.requests.CreateEventRequest
import com.example.hive.model.network.responses.CreateEventResponse
import com.example.hive.model.network.responses.UserResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class EventCreationViewModel (private val repository: EventRepository): ViewModel() {
    val eventCreationPage: MutableLiveData<Resource<CreateEventResponse>> = MutableLiveData()

    fun createEventVM(request: CreateEventRequest) = viewModelScope.launch {
        eventCreationPage.postValue(Resource.Loading())
        val response = repository.postEventrR(request)
        //eventCreationPage.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<CreateEventResponse>): Resource<UserResponse> {
        if (response.isSuccessful) {
            response.body()?.let { CreateEventResponse ->
                //return Resource.Success(CreateEventResponse)
            }
        }
        return Resource.Error(response.message())
    }
}