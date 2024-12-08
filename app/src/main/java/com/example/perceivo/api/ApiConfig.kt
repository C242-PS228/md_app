package com.example.perceivo.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://perceivo-backend-api-132823030367.asia-southeast2.run.app/1.0.0-latest/"

    fun getBaseUrl(): String {
        return BASE_URL
    }

    fun getApiService(): ApiService {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        // Custom Interceptor untuk logging error
        val errorInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            if (!response.isSuccessful) {
                Log.e("ApiError", "HTTP Error: ${response.code} - ${response.message}")
                response.body?.let { responseBody->
                    val bufferedResponse = responseBody.source().buffer
                    Log.e("ApiError", "Error Body: ${bufferedResponse.readUtf8()}")
                }
            }
            response
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(errorInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
}