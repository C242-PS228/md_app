package com.example.cobaapi.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cobaapi.data.model.ProfileData
import com.example.cobaapi.data.model.ProfileResponse
import com.example.cobaapi.data.model.TokenResponse
import com.example.cobaapi.repository.AuthRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    private val _profile = MutableLiveData<ProfileData?>()
    val profile: LiveData<ProfileData?> get() = _profile

    fun login(email: String, password: String) {
        repository.login(email, password).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    _token.postValue(response.body()?.data?.token)
                } else {
                    _token.postValue(null)
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                _token.postValue(null)
            }
        })
    }

    fun register(email: String, password: String, username: String, fullname: String, address: String) {
        repository.register(email, password, username, fullname, address).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    _token.postValue(response.body()?.data?.token)
                } else {
                    _token.postValue(null)
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                _token.postValue(null)
            }
        })
    }

    fun getProfile(token: String) {
        repository.getProfile(token).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    _profile.postValue(response.body()?.data?.firstOrNull())
                } else {
                    _profile.postValue(null)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                _profile.postValue(null)
            }
        })
    }
}
