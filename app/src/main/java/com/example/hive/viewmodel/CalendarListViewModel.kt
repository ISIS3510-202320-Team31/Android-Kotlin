package com.example.hive.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.R
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class CalendarListViewModel(private val user_id: String, private val future: String, private val context: Context) : ViewModel() {

    val eventsPage: MutableLiveData<Resource<List<EventResponse>>> = MutableLiveData()
    val eventsPageCreated: MutableLiveData<Resource<List<EventResponse>>> = MutableLiveData()
    val repository = EventRepository(context)

    init{
        getEventsByDateAndUserVM(user_id,future)
        getEventsCreatedByUserVM(user_id)
    }

    private fun getEventsByDateAndUserVM(user_id: String,future: String) = viewModelScope.launch {
        try {
            eventsPage.postValue(Resource.Loading())
            val response = repository.getEventsByDateAndUserR(user_id,future)
            eventsPage.postValue(handleResponse(response))
        } catch (e: Exception) {
            //Toast.makeText(context, context.getString(R.string.error_internet), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }

    private fun getEventsCreatedByUserVM(user_id: String) = viewModelScope.launch {
        try {
            eventsPageCreated.postValue(Resource.Loading())
            val response = repository.getEventsCreatedByR(user_id)
            eventsPageCreated.postValue(handleResponse(response))
        } catch (e: Exception) {
            e.printStackTrace()
        }
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