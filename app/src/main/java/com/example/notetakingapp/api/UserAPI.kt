package com.example.notetakingapp.api

import com.example.notetakingapp.models.UserRequest
import com.example.notetakingapp.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface UserAPI {

    @POST("/user/signup")
   suspend fun signup(@Body userRequest: UserRequest): Response<UserResponse>


    @POST("/user/signin")
    suspend fun signin(@Body userRequest: UserRequest): Response<UserResponse>

}