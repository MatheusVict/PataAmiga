package com.example.pawfriend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.pawfriend.apiJsons.UserLogin
import com.example.pawfriend.databinding.ActivityChangePasswordBinding

class ChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")

        val email: String? = intent.getStringExtra("userEmail")

        if (!email.isNullOrEmpty()) binding.userEmailInput.setText(email)

        binding.SubmitNewPassword.setOnClickListener {
            submitForm()
        }
    }

    private fun submitForm() {
        binding.userEmailInput.error = validEmail()
        binding.userNewPassword.error = validPassword()

        val validEmail = binding.userEmailInput.error == null
        val validPassword = binding.userNewPassword.error == null

        val toastMessage: String = getString(R.string.toast_error_inputs)

        if (validEmail && validPassword) {


        } else Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun emailFocusListener() {
        binding.userEmailInput.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.userEmailInput.error = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.userEmailInput.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return getString(R.string.register_one_email_wrong)
        }
        return null
    }

    private fun passwordFocusListener() {
        binding.userNewPassword.setOnFocusChangeListener {_, focused ->
            if (!focused) {
                binding.userNewPassword.error = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.userNewPassword.text.toString()
        if (passwordText.isEmpty()) {
            return getString(R.string.inputEmptyError)
        }
        return null
    }
}