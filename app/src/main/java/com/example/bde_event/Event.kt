package com.example.bde_event

import java.time.LocalDate

data class Event(
    val id: Int,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val time: String,
    val location: String?,
    val type: String,
    val description: String?
)