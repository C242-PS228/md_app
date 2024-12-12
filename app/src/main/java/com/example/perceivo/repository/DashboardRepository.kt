package com.example.perceivo.repository

import android.util.Log
import com.example.perceivo.api.ApiService
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.model.TotalSentimentStatistics
import kotlinx.coroutines.flow.first

class DashboardRepository (
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
){
    suspend fun getSentimentStatistics(): TotalSentimentStatistics {
        val token = dataStoreManager.getToken.first()
        if (token.isEmpty()) throw Exception("Token is missing or invalid")

        return try {
            Log.d("DashboardRepository", "Fetching sentiment statistics with token: $token")
            val response = apiService.getDashboard("Bearer $token")
            if (response.isSuccessful) {
                Log.d("DashboardRepository", "Response: ${response.body()}")
                Log.d("API Response", response.toString())

                val totalSentimentStatistics = response.body()?.data?.totalSentimentStatistics
                    ?: throw Exception("No data found")

                Log.d("DashboardRepository", "Positive: ${totalSentimentStatistics.positive}")
                Log.d("DashboardRepository", "Negative: ${totalSentimentStatistics.negative}")
                Log.d("DashboardRepository", "Neutral: ${totalSentimentStatistics.neutral}")

                response.body()?.data?.totalSentimentStatistics ?: throw Exception("No data found")
            } else {
                Log.e("DashboardRepository", "Error fetching dashboard data: ${response.message()}")
                throw Exception("Error fetching dashboard data: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("DashboardRepository", "Error fetching dashboard data: ${e.message}", e)
            throw e
        }
    }
}
