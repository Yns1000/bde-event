package com.example.bde_event.data.model

import com.squareup.moshi.Json

data class EmailDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "email")
    val email: String
)
