package com.example.cobaapi.data.api

import com.example.cobaapi.data.model.LoginRequest
import com.example.cobaapi.data.model.ProfileResponse
import com.example.cobaapi.data.model.RegisterRequest
import com.example.cobaapi.data.model.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Call

interface AuthApi {
    @POST("/dev/login")
    fun login(@Body request: LoginRequest): Call<TokenResponse>

    @POST("/dev/register")
    fun register(@Body request: RegisterRequest): Call<TokenResponse>

    @GET("/dev/profile")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>
}