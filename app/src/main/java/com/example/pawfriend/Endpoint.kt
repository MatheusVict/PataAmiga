package com.example.pawfriend

import com.example.pawfriend.apiJsons.User
import com.example.pawfriend.apiJsons.UserLogin
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Endpoint {
    @POST("/api/user")
    fun createUser(@Body user: User): Call<User>

    @POST("/auth/user/login")
    fun login(@Body user: UserLogin): Call<Any>

    @GET("/api/user/byself")
    fun getUserProfile(): Call<User>
}