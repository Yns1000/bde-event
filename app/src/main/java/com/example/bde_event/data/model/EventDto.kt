package com.example.bde_event.data.model

import com.squareup.moshi.Json

data class EventDto(
    @Json(name = "id")
    val id: Long,

    @Json(name = "name")
    val name: String,

    @Json(name = "date")
    val date: String,

    @Json(name = "duration")
    val duration: String,

    @Json(name = "description")
    val description: String?,

    @Json(name = "lieu")
    val location: String?,

    @Json(name = "idType")
    val idType: Int,

    @Json(name = "idUser")
    val idUser: Int
)
