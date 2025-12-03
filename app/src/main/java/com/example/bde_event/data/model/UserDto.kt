package com.example.bde_event.data.model

import com.squareup.moshi.Json

data class UserDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "username")
    val username: String,

    @Json(name = "password")
    val password: String? = null
)
