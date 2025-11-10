package com.example.bde_event.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "email")
data class EmailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String
)
