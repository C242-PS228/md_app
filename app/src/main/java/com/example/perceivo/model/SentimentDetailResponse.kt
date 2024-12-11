package com.example.perceivo.model

data class SentimentDetailResponse(
    val data: Data? = null,
    val version: String? = null,
    val status: String? = null
)

data class Data(
    val id: Int? = null,
    val unique_id: String? = null,
    val title: String? = null,
    val platform: String? = null,
    val sentiment_link: String? = null,
    val sentiment_created_at: String? = null,
    val sentiment_statistic_id: String? = null,
    val comments_id: String? = null,
    val tags: List<String?>? = null,
    val statistic: Statistic? = null,
    val comments: List<CommentsItem?>? = null
)

data class Statistic(
    val id: String? = null,
    val data: DataSentiment? = null,
)

data class DataSentiment(
    val assistances: List<ListAssistances?>? = null,
    val positive: Int? = null,
    val negative: Int? = null,
    val neutral: Int? = null,
    val topstatus: Topstatus? = null,
    val key_words: KeyWords? = null,
    val questions: List<QuestionsItem?>? = null,
    val resume: String? = null
)

data class ListAssistances(
    val username: String? = null,
    val text: String? = null
)

data class Topstatus(
    val positive: List<PositiveItem?>? = null,
    val negative: List<NegativeItem?>? = null
)

data class PositiveItem(
    val username: String? = null,
    val text: String? = null
)

data class NegativeItem(
    val username: String? = null,
    val text: String? = null
)

data class KeyWords(
    val positive: List<PositiveKeyword?>? = null,
    val negative: List<NegativeKeyword?>? = null,
    val graph_positive: List<GraphPositiveItem?>? = null,
    val graph_negative: List<GraphNegativeItem?>? = null
)

data class PositiveKeyword(
    val tagname: String? = null,
    val value: Int? = null
)

data class NegativeKeyword(
    val tagname: String? = null,
    val value: Int? = null
)

data class GraphPositiveItem(
    val tagname: String? = null,
    val value: Int? = null
)

data class GraphNegativeItem(
    val tagname: String? = null,
    val value: Int? = null
)

data class CommentsItem(
    val uid: String? = null,
    val createdAt: String? = null,
    val text: String? = null,
    val avatar: String? = null,
    val username: String? = null,
    val likes: Int? = null
)

data class QuestionsItem(
    val uid: Int? = null,
    val username: String? = null,
    val text: String? = null,
    val likes: Int? = null,
    val createdAt: String? = null,
    val avatar: String? = null
)






