package com.example.perceivo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.databinding.ActivitySignUpBinding
import com.example.perceivo.model.RegisterRequest
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentStatisticRepository
import com.example.perceivo.viewmodel.AuthViewModel
import com.example.perceivo.viewmodel.ViewModelFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory(
            AuthRepository(ApiConfig.getApiService(),
                dataStoreManager = DataStoreManager.getInstance(this)),
            ProfileRepository(ApiConfig.getApiService(), dataStoreManager = DataStoreManager.getInstance(this)),
            SentimentStatisticRepository(ApiConfig.getApiService(), dataStoreManager = DataStoreManager.getInstance(this))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
         setUpListener()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.registerResponse.observe(this) {response ->
            Log.d("SignUpActivity", "Register Response: $response")
            if (response.status=="success") {
                Toast.makeText(this," Registrasi berhasil, silakan login",Toast.LENGTH_SHORT).show()
                finish()
            }else {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                Log.e("SignUpActivity", "Register failed: ${response.message}")
            }
        }
        viewModel.isLoading.observe(this){isLoading ->
            Log.d("SignUpActivity", "Loading state: $isLoading")
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setUpListener() {
        binding.btnSignUp.setOnClickListener {
            val username = binding.edSignupUsername.text.toString().trim()
            val email = binding.edSignupEmail.text.toString().trim()
            val password = binding.edSignupPassword.text.toString().trim()
            val fullname = binding.edSignupFullname.text.toString().trim()
            val address = binding.edSignupAddress.text.toString().trim()


            if (email.isNotEmpty()&&password.isNotEmpty()&&username.isNotEmpty()&&fullname.isNotEmpty()&&address.isNotEmpty()) {
                val registerRequest = RegisterRequest(email, password, username, fullname, address)
                viewModel.registerUser(registerRequest)
            } else {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvSignIn.setOnClickListener {
            finish()
        }
    }
}