package com.example.perceivo.model

data class StatisticResponse(
    val status: String,
    val data: SentimentStatistic
)

data class SentimentStatistic(
    val positive: Int,
    val neutral: Int,
    val negative: Int
)
