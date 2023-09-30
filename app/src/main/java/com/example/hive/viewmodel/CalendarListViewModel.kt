package com.example.hive.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class CalendarListViewModel (private val repository: EventRepository,
                             private val user_id: String,
                             private val future: String) : ViewModel() {

    val eventsPage: MutableLiveData<Resource<List<EventResponse>>> = MutableLiveData()
    val eventsByDate: MutableLiveData<Resource<List<Pair<String, List<EventResponse>>>>> = MutableLiveData()

    init {
        getEventsByDateAndUserVM(user_id, future)
    }

    private fun getEventsByDateAndUserVM(user_id: String, future: String) = viewModelScope.launch {
        eventsPage.postValue(Resource.Loading())
        val response = repository.getEventsByDateAndUserR(user_id, future)
        println(response.body())
        eventsPage.postValue((handleResponse(response)))

        //Group events by date and update LiveData
        if (response.isSuccessful){
            response.body()?.let { events ->
                val gropupedEvents = events.groupBy { it.date }.map{
                    entry -> entry.key to entry.value
                }
                eventsByDate.postValue(Resource.Success(gropupedEvents))
            }
        }
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