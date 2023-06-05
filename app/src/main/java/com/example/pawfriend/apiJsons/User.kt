package com.example.pawfriend.apiJsons

data class User (

    val id: Long = 0,

    val name: String = "",

    val email: String = "",

    var password: String = "",

    val birth: String = "",

    val location: String = "",

    var profilePic: String? = "",

    val banner: String? = "",

    val phone: String = "",

    val instagram: String? = "",

    val facebook: String? = "",

    val whatsapp: String? = "",
    )