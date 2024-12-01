package com.example.cobaapi.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.example.cobaapi.R
import com.example.cobaapi.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvFullname: TextView
    private lateinit var tvAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvUsername = findViewById(R.id.tv_username)
        tvEmail = findViewById(R.id.tv_email)
        tvFullname = findViewById(R.id.tv_fullname)
        tvAddress = findViewById(R.id.tv_address)

        // Panggil fungsi untuk mengambil profil
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val url = "https://sentivuebe1-6dh6x3vy.b4a.run/dev/profile"

        // Ambil token dari SharedPreferences
        val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("JWT_TOKEN", null)

        if (token == null) {
            Toast.makeText(this, "Token not found, please login again", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Buat request dengan header Authorization
        val request = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if (status == "success") {
                    val data = response.getJSONArray("data").getJSONObject(0)
                    val email = data.getString("email")
                    val username = data.getString("username")
                    val fullname = data.getString("name")
                    val address = data.getString("address")

                    // Tampilkan data di TextView
                    tvEmail.text = email
                    tvUsername.text = username
                    tvFullname.text = fullname
                    tvAddress.text = address
                }
            },
            { error ->
                Toast.makeText(this, "Failed to fetch profile: ${error.message}", Toast.LENGTH_SHORT).show()
                if (error.networkResponse?.statusCode == 401) {
                    // Token tidak valid, kembali ke Login
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        // Tambahkan request ke antrian
        Volley.newRequestQueue(this).add(request)
    }
}
