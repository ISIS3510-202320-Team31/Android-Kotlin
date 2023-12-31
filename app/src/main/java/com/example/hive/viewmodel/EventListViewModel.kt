package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.responses.EventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class EventListViewModel(private val userSession: UserSession, private val context: Context) : ViewModel() {

    val eventsPage: MutableLiveData<Resource<List<EventResponse>>> = MutableLiveData()
    val repository = EventRepository(context)

    init {
        getEventsVM()
    }

    fun getEventsVM() = viewModelScope.launch {
        try {
            eventsPage.postValue(Resource.Loading())
            // Get today's date
            val response = userSession.userId?.let { repository.getSmartFeatureR(it) }
            eventsPage.postValue(response?.let { handleResponse(it) })
        }
        catch (e: Exception) {
            //Toast.makeText(context, context.getString(R.string.error_internet), Toast.LENGTH_LONG).show()
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


