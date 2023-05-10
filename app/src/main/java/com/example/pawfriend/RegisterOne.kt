package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterOne : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_one)

        // ActionBar settings
        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")

        // Getting XML in variables
        val emailInput = findViewById<EditText>(R.id.registerEmail)
        val password = findViewById<EditText>(R.id.passwordRegister)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val name = findViewById<EditText>(R.id.registerName)
        val birthDate = findViewById<EditText>(R.id.birthDate)
        val buttonLogin = findViewById<Button>(R.id.registerButtonContinue)
        var errors = 0

        // Inputs validations
        buttonLogin.setOnClickListener {
            if (emailInput.text.toString().isEmpty()) {
                emailInput.error = getString(R.string.inputEmptyError)
                errors += 1
            } else {
                emailInput.error = null
                errors -= 1
            }
            if (name.text.toString().isEmpty()) {
                name.error = getString(R.string.inputEmptyError)
                errors += 1
            } else {
                name.error = null
                errors -= 1
            }
            if (password.text.toString().isEmpty()) {
                password.error = getString(R.string.inputEmptyError)
                errors += 1
            } else {
                password.error = null
                if (confirmPassword.text.toString() != password.text.toString()) {
                    confirmPassword.error = getString(R.string.inputPasswordError)
                    errors += 1
                } else {
                    errors -= 1
                }
            }
            if (birthDate.text.toString().isEmpty()) {
                birthDate.error = getString(R.string.inputEmptyError)
                errors += 1
            } else {
                // TODO: minimun age validate
                birthDate.error = null
                errors -= 1
            }
            // TODO: connection and API validation 
            if (errors > 0) {
                val text = getString(R.string.toastErrorInputs)
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            } else {
                val intent = Intent(this, RegisterTwo::class.java)
                startActivity(intent)
            }
        }

    }
}