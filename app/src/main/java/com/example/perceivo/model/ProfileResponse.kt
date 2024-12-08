package com.example.perceivo.model

 data class ProfileResponse (
     val status: String,
     val message: String,
     val data: List<ProfileData>
 )

data class ProfileData (
    val unique_id: String,
    val role_id: String,
    val email: String,
    val username: String,
    val name: String,
    val google_id: String,
    val address: String,
    val created_at: String
)

data class ProfileRequest(
    val fullname: String,
    val username: String,
    val address: String
)