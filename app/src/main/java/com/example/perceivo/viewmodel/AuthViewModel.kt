package com.example.perceivo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceivo.model.LoginRequest
import com.example.perceivo.model.LoginResponse
import com.example.perceivo.model.RegisterRequest
import com.example.perceivo.model.RegisterResponse
import com.example.perceivo.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(request: RegisterRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authRepository.registerUser(request)
                _registerResponse.value = response
            } catch (e: HttpException) {
                Log.e("AuthViewModel", "HTTP error: ${e.code()} - ${e.message()}")
                val errorResponse = e.response()?.errorBody()?.string()
                Log.e("AuthViewModel", "Error response body: $errorResponse")
            }
            catch (e: Exception) {
                Log.e("AuthViewModel", "Error during register: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loginUser(request: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authRepository.loginUser(request)
                _loginResponse.value = response

                if (response.status == "success") {
                    response.token.let {
                        authRepository.saveToken(it)
                    }
                } else {
                    Log.e("AuthViewModel", "Login failed with message: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error during login: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}