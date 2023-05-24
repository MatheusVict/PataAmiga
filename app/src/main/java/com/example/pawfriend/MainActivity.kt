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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#FFFFFFFF")

        // TODO: retrofit
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        /*if (isNetworkAvailable(this)) {
            Toast.makeText(applicationContext, "conectado", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, "sem conexão", Toast.LENGTH_LONG).show()
        }*/

        val sharedPreferences = this.getSharedPreferences("login_credentials", Context.MODE_PRIVATE)
        val emailSaved: String? = sharedPreferences.getString("email", null)
        val passwordSaved: String? = sharedPreferences.getString("password", null)
        Log.i("MYSHERED", "email: $emailSaved, pwd: $passwordSaved")


        // TODO: try connect
        Handler(Looper.getMainLooper()).postDelayed({
            emailSaved?.let {
                passwordSaved?.let {
                    // TODO: Valid credentials in API
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                }
            }  ?: startActivity(Intent(this, RegisterOrLogin::class.java))

            finish()
        }, 3000)
    }
}