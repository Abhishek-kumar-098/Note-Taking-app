package com.example.notetakingapp.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.notetakingapp.api.UserAPI
import com.example.notetakingapp.utils.Constants.TAG
import com.example.notetakingapp.models.UserRequest
import com.example.notetakingapp.models.UserResponse
import com.example.notetakingapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val UserAPI: UserAPI)  {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData : LiveData<NetworkResult<UserResponse>>
        get() = _userResponseLiveData

    suspend fun registerUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = UserAPI.signup(userRequest)
        handleResponce(response)
    }

    suspend fun loginUser(userRequest: UserRequest){
        val response = UserAPI.signin(userRequest)
        handleResponce(response)
    }

    private fun handleResponce(response: Response<UserResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userResponseLiveData.postValue(NetworkResult.Error((errorObj.getString("message"))))

        } else {
            _userResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }



}