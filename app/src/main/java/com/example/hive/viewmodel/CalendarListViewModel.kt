package com.example.hive.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class CalendarListViewModel (private val repository: EventRepository) : ViewModel() {

    val eventsPage: MutableLiveData<Resource<List<EventResponse>>> = MutableLiveData()

    init {
        getEventsByDateVM()
    }

    private fun getEventsByDateVM() = viewModelScope.launch {
        eventsPage.postValue(Resource.Loading())
        val response = repository.getEventsByDateR()
        println(response.body())
        eventsPage.postValue((handleResponse(response)))
    }

    private fun handleResponse(response: Response<List<EventResponse>>): Resource<List<EventResponse>>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}