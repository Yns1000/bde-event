package com.example.bde_event.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    // Note : En général l'API ne renvoie PAS le mot de passe pour des raisons de sécurité,
    // mais comme il est dans ta table, on le met ici (peut-être nullable).
    @SerializedName("password")
    val password: String? = null
)