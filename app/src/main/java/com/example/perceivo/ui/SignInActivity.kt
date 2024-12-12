package com.example.perceivo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.databinding.ActivitySignInBinding
import com.example.perceivo.model.LoginRequest
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.DashboardRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentRepository
import com.example.perceivo.viewmodel.AuthViewModel
import com.example.perceivo.viewmodel.ViewModelFactory

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory(
            AuthRepository(ApiConfig.getApiService(),
                dataStoreManager = DataStoreManager.getInstance(this)),
            ProfileRepository(ApiConfig.getApiService(),
                dataStoreManager = DataStoreManager.getInstance(this)),
                DashboardRepository(ApiConfig.getApiService(), dataStoreManager = DataStoreManager.getInstance(this)),
                SentimentRepository(ApiConfig.getApiService(), dataStoreManager = DataStoreManager.getInstance(this))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpLListener()
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.loginResponse.observe(this) {response ->
            Log.d("SignInActivity", "Login Response: $response")
            if (response.status == "success") {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                Log.e("SignInActivity", "Login failed: ${response.message}")
            }
        }
        viewModel.isLoading.observe(this) {isLoading ->
            Log.d("SignInActivity", "Loading state: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setUpLListener() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.edSigninEmail.text.toString().trim()
            val password = binding.edSigninPassword.text.toString().trim()

            if(email.isNotEmpty()&&password.isNotEmpty()) {
                val loginRequest = LoginRequest(email,password)
                viewModel.loginUser(loginRequest)
            } else {
                Toast.makeText(this,"Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}