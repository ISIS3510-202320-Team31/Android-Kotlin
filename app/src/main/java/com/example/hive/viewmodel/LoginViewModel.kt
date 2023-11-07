package com.example.hive.viewmodel
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hive.model.adapters.SessionManager
import com.example.hive.model.models.UserSession
import com.example.hive.model.network.requests.LoginRequest
import com.example.hive.model.network.responses.UserResponse
import com.example.hive.model.repository.UserRepository
import com.example.hive.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val sessionManager: SessionManager, private val context: Context) : ViewModel() {

    val _loginResult = MutableLiveData<Resource<UserResponse>>()
    val repository = UserRepository(context)

    fun loginVM(request: LoginRequest) = viewModelScope.launch {
        _loginResult.postValue(Resource.Loading())
        val response = repository.loginR(request)
        _loginResult.postValue(handleResponse(response))
    }

    private fun handleResponse(response: Response<UserResponse>): Resource<UserResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                var userSession = UserSession("", resultResponse.id)
                sessionManager.saveUserSession(userSession)
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}