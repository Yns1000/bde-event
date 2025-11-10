package com.example.bde_event.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val idType: Long?,
    val idUser: Long?,
    val dateMillis: Long,
    val durationMinutes: Int,
    val description: String?
)
