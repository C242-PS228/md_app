package com.example.perceivo.repository

import android.util.Log
import com.example.perceivo.api.ApiService
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.data.MyResult
import com.example.perceivo.model.AllSentimentResponse
import com.example.perceivo.model.Sentiment
import com.example.perceivo.model.SentimentStatistic
import com.example.perceivo.model.StatisticResponse
import kotlinx.coroutines.flow.first

class SentimentStatisticRepository(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) {

    private suspend fun getToken(): String{
        return dataStoreManager.getToken.first()
    }

    suspend fun getAllSentiments(): MyResult<List<Sentiment>> {
        return try {
            val token = getToken()
            Log.d("SentimentStatisticRepo", "Fetching all sentiments with token: $token")
            val response = apiService.getAllSentiments("Bearer $token")
            Log.d("SentimentStatisticRepo", "Received response: ${response.body()}")


            if(response.isSuccessful) {
                MyResult.Success(response.body()?.sentiments?: emptyList())
            } else {
                Log.e("SentimentStatisticRepo", "Error fetching sentiments: ${response.message()}")
                MyResult.Error(Exception("Failed to fetch sentiments"))
            }
        } catch (e: Exception) {
            Log.e("SentimentStatisticRepo", "Error fetching sentiments: ${e.message}")
            MyResult.Error(Exception("Error fetching sentiments: ${e.message}"))
        }
    }



    suspend fun getSentimentStatistic(uniqueId: String): MyResult<SentimentStatistic> {
        return try {
            val token = getToken()
            Log.d("SentimentStatisticRepo", "Fetching sentiment statistics for uniqueId: $uniqueId with token: $token")
            val response = apiService.getSentimentStatistic("Bearer $token", uniqueId)
            Log.d("SentimentStatisticRepo", "Received response: ${response.body()}")


            if (response.isSuccessful) {
                val data = response.body()?.data
                // Jika data null, kita kembalikan nilai default
                MyResult.Success(data ?: SentimentStatistic(0, 0, 0))
            } else {
                // Menangani error
                Log.e("SentimentStatisticRepo", "Error fetching sentiment statistics: ${response.message()}")
                MyResult.Error(Exception("Error fetching sentiment statistics"))
            }
        } catch (e: Exception) {
            // Menangani exception
            Log.e("SentimentStatisticRepo", "Error fetching sentiment statistics: ${e.message}")
            MyResult.Error(Exception("Error fetching sentiment statistics: ${e.message}"))


        }
    }


}