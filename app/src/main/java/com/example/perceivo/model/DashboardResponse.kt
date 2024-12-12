package com.example.perceivo.model

 data class DashboardResponse (
     val version: String,
     val status: String,
     val data: DashboardData
 )

data class DashboardData (
    val totalSentimentStatistics: TotalSentimentStatistics
)


data class TotalSentimentStatistics(
    val positive: Int,
    val negative: Int,
    val neutral: Int
)

