package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#FFFFFFFF")

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}