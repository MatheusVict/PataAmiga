package com.example.pawfriend.NetworkUtils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Service {
    fun getRetrofitInstance(path: String): Retrofit {
        return Retrofit.Builder().baseUrl(path).addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}