package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val rememberInput = findViewById<RadioButton>(R.id.radioPassword)
        val loginButton = findViewById<Button>(R.id.button_login_screen)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        loginButton.setOnClickListener {
            if (emailInput.text.toString().isEmpty()) {
                emailInput.error = "Campo obrigatório"
                emailInput.setBackgroundColor(Color.RED)
            } else {
                emailInput.error = null
                emailInput.setBackgroundColor(Color.WHITE)
            }

            if (passwordInput.text.toString().isEmpty()) {
                passwordInput.error = "Campo obrigatório"
                passwordInput.setBackgroundColor(Color.RED)
            } else {
                passwordInput.error = null
                passwordInput.setBackgroundColor(Color.WHITE)
            }

            if (emailInput.text.toString().isNotEmpty() && passwordInput.text.toString().isNotEmpty()) {
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
//Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()