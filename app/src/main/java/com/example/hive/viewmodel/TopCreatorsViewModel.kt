package com.example.hive.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.responses.TopCreatorsResponse
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


class TopCreatorsViewModel(private val context: Context) : ViewModel() {

    val topCreators = MutableLiveData<Resource<List<TopCreatorsResponse>>>()
    val repository = UserRepository(context)

    init{
        getTopCreators()
    }

    fun getTopCreators() = viewModelScope.launch {
        try {
            topCreators.postValue(Resource.Loading())
            val response = repository.getTopCreatorsR()
            topCreators.postValue(handleResponse(response))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun handleResponse(response: Response<List<TopCreatorsResponse>>): Resource<List<TopCreatorsResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}