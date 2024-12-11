package com.example.perceivo.model

data class AllSentimentResponse(
    val version: String,
    val status: String,
    val data: List<SentimentData>
)

data class SentimentData(
    val id: Int,
    val unique_id: String,
    val title: String? = null,
    val user_id: Int,
    val statistic_id: String,
    val comments_id: String,
    val comments_limit: Int? = null,
    val platform: String,
    val sentiment_link: String,
    val created_at: String
)