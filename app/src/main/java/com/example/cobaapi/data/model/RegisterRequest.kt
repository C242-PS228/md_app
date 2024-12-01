package com.example.cobaapi.data.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val fullname: String,
    val address: String
)