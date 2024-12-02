package com.example.perceivo.api

import com.example.perceivo.model.LoginRequest
import com.example.perceivo.model.LoginResponse
import com.example.perceivo.model.RegisterRequest
import com.example.perceivo.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}