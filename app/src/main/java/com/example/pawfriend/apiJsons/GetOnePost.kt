package com.example.pawfriend.apiJsons

data class GetOnePost(
    val id: Long = 0,

    val postPic: String = "",

    val name: String = "",

    val race: String = "",

    val specie: String = "",

    val sex: String = "",

    val age: String = "",

    val size: String = "",

    val weight: String = "",

    val about: String = "",

    val petLocation: String = "",

    val isAdopted: Boolean,

    val isCastrated: Boolean,

    val isVaccinated: Boolean,

    val isPedigree: Boolean,

    val isDewormed: Boolean,

    val isEspecialNeeds: Boolean,

    val userId: Long,

    val userPic: String?,

    val userName: String,

    val userEmail: String = "",

    val userPhone: String = "",

    val userWhatsapp: String? = null,

    val userInstagram: String? = null,

    val userFacebook: String? = null,
)
