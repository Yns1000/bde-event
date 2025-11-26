package com.example.bde_event.data.model

import com.google.gson.annotations.SerializedName

data class EmailDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String
)