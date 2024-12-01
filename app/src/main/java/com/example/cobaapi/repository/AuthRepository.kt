package com.example.cobaapi.repository

import com.example.cobaapi.data.api.ApiClient
import com.example.cobaapi.data.api.AuthApi
import com.example.cobaapi.data.model.LoginRequest
import com.example.cobaapi.data.model.RegisterRequest

class AuthRepository {
    private val authApi: AuthApi = ApiClient.retrofit.create(AuthApi::class.java)

    fun login(email: String, password: String) = authApi.login(LoginRequest(email, password))

    fun register(email: String, password: String, username: String, fullname: String, address: String) =
        authApi.register(RegisterRequest(email, password, username, fullname, address))

    fun getProfile(token: String) = authApi.getProfile(token)
}