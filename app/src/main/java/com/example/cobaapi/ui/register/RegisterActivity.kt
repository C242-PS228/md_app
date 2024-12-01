package com.example.cobaapi.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cobaapi.R
import com.example.cobaapi.ui.login.LoginActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFullname: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnRegister: Button

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etEmail = findViewById(R.id.etEmail)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etFullname = findViewById(R.id.etFullname)
        etAddress = findViewById(R.id.etAddress)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val fullname = etFullname.text.toString()
            val address = etAddress.text.toString()

            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && fullname.isNotEmpty() && address.isNotEmpty()) {
                performRegister(email, username, password, fullname, address)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performRegister(email: String, username: String, password: String, fullname: String, address: String) {
        val url = "https://sentivuebe1-6dh6x3vy.b4a.run/dev/register"
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("username", username)
            put("fullname", fullname)
            put("address", address)
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        btnRegister.isEnabled = false // Disable button during the request

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    btnRegister.isEnabled = true
                    Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseData = response.body?.string()
                    Log.d("RegisterResponse", "Response: $responseData")
                    runOnUiThread {
                        btnRegister.isEnabled = true

                        if (!response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Error: ${response.code} - $responseData", Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        val jsonResponse = JSONObject(responseData)
                        val status = jsonResponse.optString("status", "error")
                        val message = jsonResponse.optString("message", "Unknown error")

                        if (status == "success") {
                            Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}