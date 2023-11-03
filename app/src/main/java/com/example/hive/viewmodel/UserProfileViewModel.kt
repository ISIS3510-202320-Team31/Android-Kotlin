package com.example.hive.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.network.responses.UserParticipationResponse
import com.example.hive.model.network.responses.UserResponse
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class UserProfileViewModel(private val sessionManager: SessionManager) : ViewModel() {

    //User Participation
    val userParticipation: MutableLiveData<Resource<UserParticipationResponse>> = MutableLiveData()

    //User Detail
    val userDetail: MutableLiveData<Resource<UserResponse>> = MutableLiveData()

    //LiveData for time usage
    private val _elapsedTimeLiveData = MutableLiveData<Long>()
    val elapsedTimeLiveData: LiveData<Long> = _elapsedTimeLiveData

    val repository = UserRepository()

    init{
        getUserDetailVM()
        getUserParticipationVM()
        _elapsedTimeLiveData.value = sessionManager.getElapsedTime()
    }

    fun getUserParticipationVM() = viewModelScope.launch {
        userParticipation.postValue(Resource.Loading())
        val response = sessionManager.getUserSession().userId?.let { repository.getParticipationR(it) }
        userParticipation.postValue(response?.let { handleResponseParticipation(it) })
    }

    fun getUserDetailVM()= viewModelScope.launch {
        userDetail.postValue(Resource.Loading())
        val response = sessionManager.getUserSession().userId?.let { repository.getUserByIdR(it) }
        userDetail.postValue(response?.let { handleResponseDetail(it) })
    }

    fun updateElapsedTime(){
        _elapsedTimeLiveData.value = sessionManager.getElapsedTime()
    }

    private fun handleResponseParticipation(response: Response<UserParticipationResponse>): Resource<UserParticipationResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleResponseDetail(response: Response<UserResponse>): Resource<UserResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}