package com.example.perceivo.model

data class SentimentRequest(
    val link: Any,
    val platformName: String,
    val title: String,
    val tags: Any,
    val resultLimit: Int
)