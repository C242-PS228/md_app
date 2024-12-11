package com.example.perceivo.model

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val status: String,
    val message: String,
    val version: String
)
