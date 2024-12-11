package com.example.perceivo.repository

import android.util.Log
import com.example.perceivo.api.ApiService
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.model.AllSentimentResponse
import com.example.perceivo.model.SentimentDetailResponse
import com.example.perceivo.model.SentimentRequest
import com.example.perceivo.model.SentimentResponse
import kotlinx.coroutines.flow.first

class SentimentRepository(
    private val apiService: ApiService ,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun fetchSentiment(request: SentimentRequest): SentimentResponse {
        val token = dataStoreManager.getToken.first()
        if (token.isEmpty()) throw Exception("Token is missing or invalid")
        Log.d("SentimentRepository", "Request: $request")

        return try {
            Log.d("SentimentRepository", "Sending request at: ${System.currentTimeMillis()}")
            val response = apiService.getSentiment("Bearer $token", request)
            Log.d("SentimentRepository", "Received response at: ${System.currentTimeMillis()}")

            // Log response data
            Log.d("SentimentRepository", "Response: $response")

            // Log each field of SentimentResponse
            Log.d("SentimentRepository", "Version: ${response.version}")
            Log.d("SentimentRepository", "Status: ${response.status}")
            Log.d("SentimentRepository", "Message: ${response.message}")
            Log.d("SentimentRepository", "Sentiment ID: ${response.sentiment_id}")
            Log.d("SentimentRepository", "Statistic ID: ${response.statistic_id}")
            Log.d("SentimentRepository", "Links: ${response.links}")
            Log.d("SentimentRepository", "Platform: ${response.platform}")
            response
        } catch (e: Exception) {
            throw Exception("Failed to fetch sentiment: ${e.message}", e)
        }
    }

    // Detail Sentiment
    suspend fun fetchSentimentDetail(sentimentId: String): SentimentDetailResponse {
        val token = dataStoreManager.getToken.first()
        if (token.isEmpty()) throw Exception("Token is missing or invalid")

        return try {
            val responseSentiment = apiService.getSentimentDetails("Bearer $token", sentimentId)
            Log.d("SentimentRepository", "Response: $responseSentiment")
            responseSentiment
        } catch (e: Exception) {
            throw Exception("Failed to fetch sentiment detail: ${e.message}", e)
        }
    }

    // Fetch all sentiment
    suspend fun fetchAllSentiment(): AllSentimentResponse {
        val token = dataStoreManager.getToken.first()
        if (token.isEmpty()) throw Exception("Token is missing or invalid")

        return try {
            val response = apiService.getAllSentiments("Bearer $token")
            Log.d("SentimentRepository", "Fetched all sentiments: $response")
            response
        } catch (e: Exception) {
            throw Exception("Failed to fetch all sentiments: ${e.message}", e)
        }
    }
}