package com.example.cobaapi.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.cobaapi.R
import com.example.cobaapi.ui.profile.ProfileActivity
import com.example.cobaapi.ui.register.RegisterActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        // Login button logic
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call login function from ViewModel or Repository
                performLogin(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirect to Register Activity
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin(email: String, password: String) {
        val url = "https://sentivuebe1-6dh6x3vy.b4a.run/dev/login"
        val requestBody = JSONObject()
        requestBody.put("email", email)
        requestBody.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                // Log the response to see its structure
                Log.d("LoginResponse", response.toString())

                val status = response.getString("status")
                if (status == "success") {
                    // Extract the token directly from the response
                    val token = response.getString("token")

                    // Save token to SharedPreferences
                    val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("JWT_TOKEN", token)
                    editor.apply()

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    // Redirect to ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle case where status is not success
                    val message = response.optString("message", "Unknown error occurred")
                    Toast.makeText(this, "Login failed: $message", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add request to the queue
        Volley.newRequestQueue(this).add(request)
    }
}