package com.example.bde_event.data.model

import com.google.gson.annotations.SerializedName

data class EventDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")          // Correspond à 'name'
    val name: String,

    @SerializedName("date")          // Correspond à 'date'
    val date: String,

    @SerializedName("duration")      // Correspond à 'duration'
    val duration: String,

    @SerializedName("description")   // Correspond à 'description'
    val description: String?,

    @SerializedName("lieu")          // Correspond à la colonne 'lieu'
    val location: String?,

    @SerializedName("idType")        // Clés étrangères
    val idType: Int,

    @SerializedName("idUser")
    val idUser: Int
)