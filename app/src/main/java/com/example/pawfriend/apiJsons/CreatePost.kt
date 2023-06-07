package com.example.pawfriend.apiJsons

data class PostPets(

    val id: Long = 0,

    val name: String = "",

    val postPic: String = "",

    val race: String = "",

    val specie: String = "",

    val sex: String = "",

    val age: String = "",

    val size: String = "",

    val weight: String = "",

    val about: String = "",

    val petLocation: String = "",

    val isAdopted: Boolean = false,

    val isCastrated: Boolean = false,

    val isVaccinated: Boolean = false,

    val isPedigree: Boolean = false,

    val isDewormed: Boolean = false,

    val isEspecialNeeds: Boolean = false,
)