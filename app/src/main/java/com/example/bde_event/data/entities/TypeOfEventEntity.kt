package com.example.bde_event.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type_of_event")
data class TypeOfEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?
)
