package com.example.pawfriend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.apiJsons.UserLogin
import com.example.pawfriend.databinding.ActivityLoginBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isUserValid: Boolean = false
    private var UserToken: String = ""
    private var toastLoginMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#0CBFDE")
        emailFocusListener()
        passwordFocusListener()

        binding.buttonLoginScreen.setOnClickListener { submitForm() }
    }

    private fun submitForm() {
        binding.emailInput.error = validEmail()

        val validEmail = binding.emailInput.error == null
        val validPassword = binding.passwordInput.error == null

        val toastMessage: String = getString(R.string.toast_error_inputs)

        if (validEmail && validPassword) {
            if (binding.radioPassword.isChecked) {
                val sharedPreferences = this.getSharedPreferences("login_credentials", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("email", binding.emailInput.text.toString())
                editor.putString("password", binding.passwordInput.text.toString())
                editor.apply()
            }
            val user = UserLogin(
                email = binding.emailInput.text.toString(),
                password = binding.passwordInput.text.toString()
            )
            Log.i("APITESTE", "email e senha ${binding.emailInput.text.toString()} e senha ${binding.emailInput.text.toString()}")
            login(user){isUserValid ->
                if (isUserValid) {
                    val sharedPreferences = this.getSharedPreferences("login_credentials", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token", UserToken)
                    editor.apply()
                    val intent = Intent(this, Home::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else Toast.makeText(applicationContext, toastLoginMessage, Toast.LENGTH_LONG).show()
            }

        } else Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
    }


    private fun login(user: UserLogin, callback: (Boolean) -> Unit) {
        val retrofitClient = Service.getRetrofitInstance("http://192.168.0.107:8080")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.login(user).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    isUserValid = true
                    val gson = Gson()
                    val json = gson.toJson(response.body())
                    val jsonObject = gson.fromJson(json, JsonObject::class.java)
                    val message = jsonObject.get("message").asString
                    UserToken = message
                    Log.i(
                        "APITESTE",
                        "response: ${response.body()}" +
                                "and $message"
                    )
                    callback(isUserValid)

                } else {
                    isUserValid = false
                    Log.i("APITESTE", "response com erro da api: ${response}")
                    callback(false)
                    toastLoginMessage = "credenciais invalidas"
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                isUserValid = false
                Log.i("APITESTE", "Erro: $t e call: ${call}")
                callback(false)
                toastLoginMessage = "falha ao logar"
            }
        })
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