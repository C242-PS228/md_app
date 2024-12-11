package com.example.perceivo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.databinding.ActivitySplashScreenBinding
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.viewmodel.SplashScreenViewModel
import com.example.perceivo.viewmodel.ViewModelFactory

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel: SplashScreenViewModel by viewModels {
        ViewModelFactory(AuthRepository(ApiConfig.getApiService(), dataStoreManager = DataStoreManager.getInstance(this)),
            ProfileRepository(ApiConfig.getApiService(), dataStoreManager = DataStoreManager.getInstance(this))
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkToken()


    }

    private fun checkToken() {
        viewModel.getToken().observe(this) { token ->
            if (token.isNullOrEmpty()) {
                // Token is missing or empty, point to SignInActivity
                Log.d("SplashScreenActivity", "Token not found, move to SignInActivity")
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            } else {
                // Token exists, move to MainActivity
                Log.d("SplashScreenActivity", "Token found, move to MainActivity")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}