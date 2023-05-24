package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.example.pawfriend.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")
        emailFocusListener()
        validPassword()

        binding.buttonLoginScreen.setOnClickListener { submitForm() }
    }

    private fun submitForm() {
        binding.emailInput.error = validEmail()

        val validEmail = binding.emailInput.error == null

        val toastMessage: String = getString(R.string.toast_error_inputs)

        if (validEmail) {
            // TODO: Login API and validation
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        } else Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun emailFocusListener() {
        binding.emailInput.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.emailInput.error = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailInput.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return getString(R.string.register_one_email_wrong)
        }
        return null
    }

    private fun passwordFocusListener() {
        binding.passwordInput.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.passwordInput.error = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordInput.text.toString()
        if (passwordText.isEmpty()) {
            return getString(R.string.inputEmptyError)
        }
        return null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}