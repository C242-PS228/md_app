package com.example.perceivo.ui.fragment.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceivo.model.SentimentData
import com.example.perceivo.model.TotalSentimentStatistics
import com.example.perceivo.repository.DashboardRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SentimentRepository,
    private val dashboardRepository: DashboardRepository,
    private val userRepository: ProfileRepository
) : ViewModel() {

    private val _sentimentData = MutableLiveData<List<SentimentData>>()
    val sentimentData: LiveData<List<SentimentData>> get() = _sentimentData

    private val _username = MutableLiveData<String> ()
    val username: LiveData<String> get() = _username

    private val _sentimentStatistics = MutableLiveData<TotalSentimentStatistics>()
    val sentimentStatistics: LiveData<TotalSentimentStatistics> get() = _sentimentStatistics

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private fun launchCoroutine(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchSentimentData() {
        launchCoroutine {
            val response = repository.fetchAllSentiment()
            _sentimentData.value = response.data
        }
    }

    fun getSentimentStatistics() {
        _isLoading.value = true
        launchCoroutine {
            val sentimentData = dashboardRepository.getSentimentStatistics()
            _sentimentStatistics.value = sentimentData
            _isLoading.value = false
        }
    }

    fun fetchUsername() {
        launchCoroutine {
            val profileResponse = userRepository.getProfile()
            if (profileResponse.status == "success") {
                val user = profileResponse.data.firstOrNull()
                if (user != null) {
                    _username.postValue(user.username)
                } else {
                    Log.e("HomeViewModel", "User  data is empty")
                }
            } else {
                Log.e("HomeViewModel", "Failed to fetch profile: ${profileResponse.message}")
            }
        }
    }
}