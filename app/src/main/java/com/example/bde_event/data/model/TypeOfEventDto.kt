package com.example.bde_event.data.model

import com.squareup.moshi.Json

data class TypeOfEventDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String?
)
