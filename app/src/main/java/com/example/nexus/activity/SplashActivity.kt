package com.example.nexus.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.nexus.R

class SplashActivity : AppCompatActivity() {
    private val delay = 1300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val nextActivity = if (isLoggedIn) HomeActivity::class.java else LoginActivity::class.java
        val intent = Intent(this, nextActivity)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        },delay)
    }
}