package com.example.perceivo.api

import com.example.perceivo.model.AllSentimentResponse
import com.example.perceivo.model.ChangePasswordRequest
import com.example.perceivo.model.ChangePasswordResponse
import com.example.perceivo.model.LoginRequest
import com.example.perceivo.model.LoginResponse
import com.example.perceivo.model.ProfileRequest
import com.example.perceivo.model.ProfileResponse
import com.example.perceivo.model.RegisterRequest
import com.example.perceivo.model.RegisterResponse
import com.example.perceivo.model.StatisticResponse
import com.example.perceivo.model.UpdateProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profileRequest: ProfileRequest
    ): UpdateProfileResponse

    @PUT("profile/changepassword")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body changePasswordRequest: ChangePasswordRequest
    ): ChangePasswordResponse

    @GET("sentiment")
    suspend fun getAllSentiments(
        @Header("Authorization") token: String
    ): Response<AllSentimentResponse>

    @GET("sentiment/{unique_id}/statistic")
    suspend fun getSentimentStatistic(
        @Header("Authorization") token: String,
        @Path("unique_id") uniqueId: String
    ): Response<StatisticResponse>

}