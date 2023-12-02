package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.requests.EditEventRequest
import com.example.hive.model.network.responses.EditEventResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class EventEditViewModel (context: Context): ViewModel() {
    val eventEditPage: MutableLiveData<Resource<EditEventResponse>> = MutableLiveData()
    val repository = EventRepository(context)

    fun editEventVM(id: String, request: EditEventRequest) = viewModelScope.launch {
        eventEditPage.postValue(Resource.Loading())
        val response = repository.editEventrR(id, request)
        eventEditPage.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<EditEventResponse>): Resource<EditEventResponse> {
        if (response.isSuccessful) {
            response.body()?.let { EditEventResponse ->
                return Resource.Success(EditEventResponse)
            }
        }
        return Resource.Error(response.message())
    }
}