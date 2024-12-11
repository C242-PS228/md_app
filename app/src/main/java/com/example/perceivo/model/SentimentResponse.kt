package com.example.perceivo.model

data class SentimentResponse(
    val version: String,
    val status: String,
    val message: String,
    val title: String,
    val sentiment_id: String,
    val comments_limit: Int,
    val statistic_id: String,
    val links: String,
    val platform: String
)