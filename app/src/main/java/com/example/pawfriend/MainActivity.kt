package com.example.pawfriend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.pawfriend.NetworkUtils.Service
import com.example.pawfriend.NetworkUtils.isNetworkAvailable
import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.apiJsons.UserLogin
import com.example.pawfriend.databinding.ActivityMainBinding
import com.example.pawfriend.global.AppGlobals
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isUserValid: Boolean = false
    private var UserToken: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#FFFFFFFF")

        val sharedPreferences = this.getSharedPreferences("login_credentials", Context.MODE_PRIVATE)
        val emailSaved: String? = sharedPreferences.getString("email", null)
        val passwordSaved: String? = sharedPreferences.getString("password", null)

        Handler(Looper.getMainLooper()).postDelayed({
            emailSaved?.let { email ->
                passwordSaved?.let { password ->
                   if (isNetworkAvailable(this)) {
                       val user = UserLogin(
                           email = email,
                           password = password
                       )
                       login(user) { isUserValid ->
                           Log.i("APITESTE", "email: $emailSaved e pass: $passwordSaved")
                           if (isUserValid) {
                               val editor = sharedPreferences.edit()
                               editor.putString("token", UserToken)
                               editor.apply()
                               val intent = Intent(this, Home::class.java)
                               intent.flags =
                                   Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                               startActivity(intent)

                           } else {
                               Toast.makeText(this, "Erro ao logar", Toast.LENGTH_SHORT).show()
                               val intent = Intent(this, RegisterOrLogin::class.java)
                               startActivity(intent)
                               finish()
                           }

                       }
                   } else {
                       Toast.makeText(this, "Sem Conexão com a internet", Toast.LENGTH_SHORT).show()
                   }
                }
            } ?: run {
                val intent = Intent(this, RegisterOrLogin::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }

    private fun login(user: UserLogin, callback: (Boolean) -> Unit) {
        val retrofitClient = Service.getRetrofitInstance(AppGlobals.apiUrl, context = this)
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
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                isUserValid = false
                Log.i("APITESTE", "Erro: $t e call: ${call}")
                callback(false)
            }
        })


    }
}