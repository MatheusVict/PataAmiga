package com.example.pawfriend

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RegisterOne : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_one)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")
    }
}