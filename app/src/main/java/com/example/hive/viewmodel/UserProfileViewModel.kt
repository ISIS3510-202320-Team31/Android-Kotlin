package com.example.hive.viewmodel

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.network.responses.EventDetailResponse
import com.example.hive.model.network.responses.UserParticipationResponse
import com.example.hive.model.network.responses.UserResponse
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class UserProfileViewModel(private val repository: UserRepository, private val sessionManager: SessionManager) : ViewModel() {

    //User Participation
    val userParticipation: MutableLiveData<Resource<UserParticipationResponse>> = MutableLiveData()

    //LiveData for time usage
    private val _elapsedTimeLiveData = MutableLiveData<Long>()
    val elapsedTimeLiveData: LiveData<Long> = _elapsedTimeLiveData

    init{
        getUserParticipationVM()
        _elapsedTimeLiveData.value = sessionManager.getElapsedTime()
    }

    fun getUserParticipationVM() = viewModelScope.launch {
        userParticipation.postValue(Resource.Loading())
        val response = sessionManager.getUserSession().userId?.let { repository.getParticipationR(it) }
        userParticipation.postValue(response?.let { handleResponse(it) })
    }

    fun updateElapsedTime(){
        _elapsedTimeLiveData.value = sessionManager.getElapsedTime()
    }

    private fun handleResponse(response: Response<UserParticipationResponse>): Resource<UserParticipationResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}