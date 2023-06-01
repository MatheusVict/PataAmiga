package com.example.pawfriend.NetworkUtils

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun getTokenSharedPreferences(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("login_credentials", Context.MODE_PRIVATE)
    Log.i("APITEST", "o token do contexto: ${sharedPreferences.getString("token", null)}")
    return  sharedPreferences.getString("token", null)
}

object Service {
    fun getRetrofitInstance(path: String, context: Context ): Retrofit {
        val token: String? = getTokenSharedPreferences(context)
        val client = OkHttpClient.Builder()
            .addInterceptor{chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }.build()

        return Retrofit
            .Builder()
            .baseUrl(path)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}