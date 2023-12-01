package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.network.responses.CategoryResponse
import com.example.hive.model.network.responses.TopPartnersResponse
import com.example.hive.model.network.responses.UserParticipationResponse
import com.example.hive.model.network.responses.UserResponse
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class UserProfileViewModel(private val sessionManager: SessionManager, private val context: Context) : ViewModel() {

    //User Participation
    val userParticipation: MutableLiveData<Resource<UserParticipationResponse>> = MutableLiveData()

    //User Detail
    val userDetail: MutableLiveData<Resource<UserResponse>> = MutableLiveData()

    //Category Chart
    val categoryChart: MutableLiveData<Resource<List<CategoryResponse>>> = MutableLiveData()

    //Top Partners
    val topPartners: MutableLiveData<Resource<List<TopPartnersResponse>>> = MutableLiveData()

    //LiveData for time usage
    private val _elapsedTimeLiveData = MutableLiveData<Long>()
    val elapsedTimeLiveData: LiveData<Long> = _elapsedTimeLiveData

    val repository = UserRepository(context)

    init{
        getUserDetailVM()
        getUserParticipationVM()
        getCategoryChartVM()
        getTopPartnersVM()
        _elapsedTimeLiveData.value = sessionManager.getElapsedTime()
    }

    private fun getTopPartnersVM() = viewModelScope.launch {
        try {
            topPartners.postValue(Resource.Loading())
            val response = sessionManager.getUserSession().userId?.let { repository.getTopPartnersR(it) }
            topPartners.postValue(response?.let { handleResponseTopPartners(it) })
        }
        catch (e: Exception) {
            //Toast.makeText(context, context.getString(R.string.error_internet), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun getCategoryChartVM() = viewModelScope.launch {
        try {
            categoryChart.postValue(Resource.Loading())
            val response = sessionManager.getUserSession().userId?.let { repository.getCategoriesR(it) }
            categoryChart.postValue(response?.let { handleResponseCategoryChart(it) })
        }
        catch (e: Exception) {
            //Toast.makeText(context, context.getString(R.string.error_internet), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
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

    private fun handleResponseTopPartners(response: Response<List<TopPartnersResponse>>): Resource<List<TopPartnersResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleResponseCategoryChart(response: Response<List<CategoryResponse>>): Resource<List<CategoryResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
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