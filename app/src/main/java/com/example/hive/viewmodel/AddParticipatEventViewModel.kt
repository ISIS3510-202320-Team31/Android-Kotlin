package com.example.hive.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.R
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.repository.EventRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class AddParticipatEventViewModel(context: Context) : ViewModel()  {
    val addParticipatEvent: MutableLiveData<Resource<EventDetailResponse>> = MutableLiveData()
    val deleteParticipatEvent: MutableLiveData<Resource<EventDetailResponse>> = MutableLiveData()
    val repository = EventRepository(context)
    val context = context

    fun addParticipatEventVM(eventID: String, userID: String) = viewModelScope.launch {
        try {
            addParticipatEvent.postValue(Resource.Loading())
            val peticion = repository.addParticipatEventR(userID, eventID)
            addParticipatEvent.postValue(handleAddParticipatEventResponse(peticion))
        } catch (e: Exception) {
            //Toast.makeText(context, context.getString(R.string.error_internet), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun deleteParticipatEventVM(eventID: String, userID: String) = viewModelScope.launch {
        try {
            deleteParticipatEvent.postValue(Resource.Loading())
            val peticion = repository.deleteParticipatEventR(userID, eventID)
            deleteParticipatEvent.postValue(handleAddParticipatEventResponse(peticion))
        }
        catch (e: Exception) {
            //Toast.makeText(context, context.getString(R.string.error_internet), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
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