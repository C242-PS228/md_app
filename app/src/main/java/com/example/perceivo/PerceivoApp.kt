package com.example.perceivo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class PerceivoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Force to use light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}