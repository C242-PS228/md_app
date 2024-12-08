package com.example.perceivo.repository

import android.util.Log
import com.example.perceivo.api.ApiService
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.model.ChangePasswordRequest
import com.example.perceivo.model.ChangePasswordResponse
import com.example.perceivo.model.ProfileRequest
import com.example.perceivo.model.ProfileResponse
import com.example.perceivo.model.UpdateProfileResponse
import kotlinx.coroutines.flow.first


class ProfileRepository(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) {
    suspend fun getProfile(): ProfileResponse {
        val token = dataStoreManager.getToken.first()
        if (token.isEmpty()) throw Exception ("Token is missing or invalid")
        return try {
            Log.d("ProfileRepository", "Fetching profile with token: $token")
            val response = apiService.getProfile("Bearer $token")
            Log.d("ProfileRepository", "Profile fetched successfully: ${response.data}")
            response
        } catch (e:Exception) {
            Log.e("ProfileRepository", "Error fetching profile: ${e.message}", e)
            throw e
        }
    }
    suspend fun updateProfile(profileRequest: ProfileRequest): UpdateProfileResponse {
        val token = dataStoreManager.getToken.first()
        if (token.isEmpty()) throw Exception("Token is missing or invalid")
        return  try {
            Log.d("ProfileRepository", "Updating profile with token: $token")
            Log.d("ProfileRepository", "Profile update request: $profileRequest")
            val response = apiService.updateProfile("Bearer $token", profileRequest)
            Log.d("ProfileRepository", "Profile updated successfully: ${response.message}")
            response
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error updating profile: ${e.message}", e)
            throw e
        }
    }

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): ChangePasswordResponse{
        val token = dataStoreManager.getToken.first()
        if (token.isEmpty()) throw Exception("Token is missing or invalid")
        return try {
            Log.d("ProfileRepository", "Changing password with token: $token")
            Log.d("ProfileRepository", "Password change request: $changePasswordRequest")
            val response = apiService.changePassword("Bearer $token", changePasswordRequest)
            Log.d("ProfileRepository", "Password changed successfully: ${response.message}")
            response
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error changing password: ${e.message}", e)
            throw e
        }
    }
}