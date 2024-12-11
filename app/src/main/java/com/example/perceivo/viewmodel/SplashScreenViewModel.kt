package com.example.perceivo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceivo.repository.AuthRepository
import kotlinx.coroutines.launch

class SplashScreenViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun getToken():LiveData<String?> {
        val liveData = MutableLiveData<String?>()
        viewModelScope.launch {
            try {
                val token = authRepository.getToken() // Mengambil token dari DataStore
                liveData.postValue(token)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error fetching token", e)
                liveData.postValue(null)  // Token kosong atau terjadi error
            }
        }
        return liveData

    }
}