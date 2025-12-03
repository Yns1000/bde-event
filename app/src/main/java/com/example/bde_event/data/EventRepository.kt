package com.example.bde_event.data.repository

import com.example.bde_event.Event

interface EventRepository {
    suspend fun getEvents(): List<Event>
    suspend fun addEvent(event: Event)
}