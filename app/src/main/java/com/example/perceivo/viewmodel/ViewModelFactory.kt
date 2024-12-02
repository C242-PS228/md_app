package com.example.perceivo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.perceivo.repository.AuthRepository

class ViewModelFactory (
    private val authRepository: AuthRepository

): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->{
                AuthViewModel(authRepository) as T
            }
                modelClass.isAssignableFrom(SplashScreenViewModel::class.java) ->{
                    SplashScreenViewModel(authRepository) as T
                }else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}