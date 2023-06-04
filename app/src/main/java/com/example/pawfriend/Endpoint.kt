package com.example.pawfriend

import com.example.pawfriend.apiJsons.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface Endpoint {
    @POST("/api/user")
    fun createUser(@Body user: User): Call<User>

    @POST("/auth/user/login")
    fun login(@Body user: UserLogin): Call<Any>

    @GET("/api/user/byself")
    fun getUserProfile(): Call<User>

    @PUT("/api/user")
    fun updateUserProfile(@Body user: UserUpdate): Call<User>

    @GET("/api/postsPets")
    fun getAllPosts(): Call<List<ListPostsPets>>

    @GET("/api/postsPets/users")
    fun getAllPostsFromUser(): Call<List<ListPostsPets>>

    @POST("/api/postsPets")
    fun createPostPets(@Body postPets: CreatePostPets): Call<CreatePostPets>

    @GET("/api/postsPets/{id}")
    fun getOnePostForId(@Path(value = "id") id: Long): Call<GetOnePost>
}