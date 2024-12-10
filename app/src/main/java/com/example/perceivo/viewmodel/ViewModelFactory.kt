package com.example.perceivo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentStatisticRepository
import com.example.perceivo.ui.fragment.home.HomeViewModel
import com.example.perceivo.ui.fragment.profile.ProfileViewModel

class ViewModelFactory (
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val sentimentStatisticRepository: SentimentStatisticRepository

): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->{
                AuthViewModel(authRepository) as T
            }
                modelClass.isAssignableFrom(SplashScreenViewModel::class.java) ->{
                    SplashScreenViewModel(authRepository) as T
                }
            modelClass.isAssignableFrom(ProfileViewModel::class.java)-> {
                ProfileViewModel(profileRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(sentimentStatisticRepository, profileRepository) as T
            }
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}