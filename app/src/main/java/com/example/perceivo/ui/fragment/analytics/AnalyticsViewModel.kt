package com.example.perceivo.ui.fragment.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceivo.model.SentimentRequest
import com.example.perceivo.model.SentimentResponse
import com.example.perceivo.repository.SentimentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalyticsViewModel(private val repository: SentimentRepository) : ViewModel() {

    private val _sentimentResponse = MutableStateFlow<SentimentResponse?>(null)
    val sentimentResponse: StateFlow<SentimentResponse?> = _sentimentResponse

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchSentiment(request: SentimentRequest) {
        viewModelScope.launch {
            try {
                val response = repository.fetchSentiment(request)
                _sentimentResponse.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}