package com.example.hive.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class EventListViewModel(private val repository: EventRepository, private val userSession: UserSession) : ViewModel() {

    val eventsPage: MutableLiveData<Resource<List<EventResponse>>> = MutableLiveData()

    init {
        getEventsVM()
    }

    private fun getEventsVM() = viewModelScope.launch {
        eventsPage.postValue(Resource.Loading())
        // Get today's date
        val response = userSession.userId?.let { repository.getSmartFeatureR(it) }
        eventsPage.postValue(response?.let { handleResponse(it) })
    }

    private fun handleResponse(response: Response<List<EventResponse>>): Resource<List<EventResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}


