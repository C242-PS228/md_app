package com.example.perceivo.model

data class AllSentimentResponse (
    val status: String,
    val sentiments: List<Sentiment>
)

data class Sentiment (
    val unique_id: String,
    val platform: String,
    val commentsProcessed: Int,
    val createdAt: String
    )