package com.example.hive.viewmodel
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.network.requests.RegisterRequest
import com.example.hive.model.network.responses.UserResponse
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class SignUpViewModel(private val context: Context) : ViewModel() {

    val registerPage: MutableLiveData<Resource<UserResponse>> = MutableLiveData()
    val repository = UserRepository(context)

    fun registerVM(request: RegisterRequest) = viewModelScope.launch {
        registerPage.postValue(Resource.Loading())
        val response = repository.registerR(request)
        registerPage.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<UserResponse>): Resource<UserResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }




}