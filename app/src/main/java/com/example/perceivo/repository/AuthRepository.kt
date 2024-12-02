package com.example.perceivo.repository

import android.util.Log
import com.example.perceivo.api.ApiService
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.model.LoginRequest
import com.example.perceivo.model.LoginResponse
import com.example.perceivo.model.RegisterRequest
import com.example.perceivo.model.RegisterResponse
import kotlinx.coroutines.flow.firstOrNull

class AuthRepository(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun registerUser(request: RegisterRequest) : RegisterResponse{
        return try {
           val response = apiService.register(request)
            Log.d("AuthRepository", "Register response: ${response.message}")
            response
        } catch (e:Exception) {
            Log.e("AuthRepository", "Error during register request: ${e.localizedMessage}", e)
            throw e
        }
    }
    suspend fun loginUser(request: LoginRequest) : LoginResponse {
        return try {
            val response = apiService.login(request)
            Log.d("AuthRepository", "Login response: $response")
            if (response.status == "success" && !response.token.isNullOrEmpty()) {
                saveToken(response.token)
                Log.d("AuthRepository", "Token berhasil disimpan: ${response.token}")
            } else {
                Log.e("AuthRepository", "Token kosong atau login gagal")
                throw Exception("Login failed: ${response.message}")
            }

            response
        } catch (e:Exception) {
            Log.e("AuthRepository", "Error during login request: ${e.localizedMessage}", e)
            throw e
        }
    }
    suspend fun saveToken(token: String) {
        Log.d("AuthRepository", "Token berhasil disimpan: $token")
        dataStoreManager.saveToken(token)
    }

    suspend fun getToken(): String? {
        return dataStoreManager.getToken.firstOrNull()

    }

    suspend fun clearToken() {
        dataStoreManager.clearToken()
    }


}