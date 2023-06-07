package com.example.pawfriend

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.apiJsons.ChangeUserPassword
import com.example.pawfriend.databinding.ActivityChangePasswordBinding
import com.example.pawfriend.global.AppGlobals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")

        emailFocusListener()
        passwordFocusListener()

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
            val user = ChangeUserPassword(
                email = binding.userEmailInput.text.toString(),
                newPassword = binding.userNewPassword.text.toString()
            )
            changeUserPassword(
                user
            )

        } else Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
    }

    private fun changeUserPassword(newCredentials: ChangeUserPassword) {
        val retrofitClient =
            Service.getRetrofitInstance(AppGlobals.apiUrl, context = this)
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.changeUserPassword(newCredentials).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, getString(R.string.change_password), Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(applicationContext, Login::class.java)
                    startActivity(intent)
                    finish()
                } else Toast.makeText(
                    applicationContext,
                    getString(R.string.change_password_user_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.api_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun emailFocusListener() {
        binding.userEmailInput.setOnFocusChangeListener { _, focused ->
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
        binding.userNewPassword.setOnFocusChangeListener { _, focused ->
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