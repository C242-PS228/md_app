package com.example.perceivo.ui.fragment.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceivo.data.MyResult
import com.example.perceivo.model.Sentiment
import com.example.perceivo.model.SentimentStatistic
import com.example.perceivo.model.StatisticResponse
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentStatisticRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: SentimentStatisticRepository,
    private val userRepository: ProfileRepository) : ViewModel() {

    private val _sentiments = MutableLiveData<MyResult<List<Sentiment>>>()
    val sentiments: LiveData<MyResult<List<Sentiment>>> get() = _sentiments

    private val _sentimentStatistic = MutableLiveData<MyResult<SentimentStatistic>> ()
    val sentimentStatistic: LiveData<MyResult<SentimentStatistic>> get() = _sentimentStatistic

    private val _username = MutableLiveData<String> ()
    val username: LiveData<String> get() = _username



    fun getAllSentiments() {
        viewModelScope.launch {
            _sentiments.value = MyResult.Loading
            val result = repository.getAllSentiments()
            Log.d("HomeViewModel", "Received sentiments: $result")
            _sentiments.value = result
        }
    }

    fun fetchUsername() {
        viewModelScope.launch {
            try {
                val profileResponse = userRepository.getProfile()
                if (profileResponse.status == "success") {
                    val user = profileResponse.data.firstOrNull()
                    if (user != null) {
                        _username.postValue(user.username) // Ambil username dari data profil
                    } else {
                        Log.e("HomeViewModel", "User data is empty")
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch profile: ${profileResponse.message}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching profile: ${e.message}")
            }
        }
    }

    fun getSentimentStatistic(uniqueId: String) {
        viewModelScope.launch {
            _sentimentStatistic.value = MyResult.Loading
            Log.d("HomeViewModel", "Fetching sentiment statistics for uniqueId: $uniqueId")

            if (uniqueId.isNotEmpty()) {
                val result = repository.getSentimentStatistic(uniqueId)
                Log.d("HomeViewModel", "Received sentiment statistics: $result")
                _sentimentStatistic.value = result
            } else {
                Log.e("HomeViewModel", "Invalid uniqueId: $uniqueId")
                _sentimentStatistic.value = MyResult.Error(Exception("Invalid uniqueId"))
            }

        }
    }

}