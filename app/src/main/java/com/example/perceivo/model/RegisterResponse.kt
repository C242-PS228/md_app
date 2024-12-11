package com.example.perceivo.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String,
    val fullname: String,
    val address: String
)


data class RegisterResponse(
    val status: String,
    val message: String,
    val version: String
)
