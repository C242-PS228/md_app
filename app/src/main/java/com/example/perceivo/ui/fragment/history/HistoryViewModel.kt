package com.example.perceivo.ui.fragment.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceivo.api.ApiService
import com.example.perceivo.model.SentimentData
import com.example.perceivo.repository.SentimentRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: SentimentRepository) : ViewModel() {

    private val _sentimentData = MutableLiveData<List<SentimentData>>()
    val sentimentData: LiveData<List<SentimentData>> get() = _sentimentData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchSentimentData() {
        viewModelScope.launch {
            try {
                val response = repository.fetchAllSentiment()
                _sentimentData.value = response.data
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error fetching data", e)
                _error.value = e.message
            }
        }
    }
}
