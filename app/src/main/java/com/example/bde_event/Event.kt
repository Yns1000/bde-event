// kotlin
package com.example.bde_event

data class Event(
    val id: Long? = null,
    val name: String,
    val idType: Long?,
    val idUser: Long?,
    val dateMillis: Long,      // epoch millis
    val durationMinutes: Int,  // durée en minutes
    val description: String?
)