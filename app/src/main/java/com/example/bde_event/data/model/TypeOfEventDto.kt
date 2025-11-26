package com.example.bde_event.data.model

import com.google.gson.annotations.SerializedName

data class TypeOfEventDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?
)