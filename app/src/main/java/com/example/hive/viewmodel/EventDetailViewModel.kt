package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class EventDetailViewModel(private val id: String, private val context: Context) : ViewModel() {
    val eventById: MutableLiveData<Resource<EventDetailResponse>> = MutableLiveData()
    val repository = EventRepository(context)

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