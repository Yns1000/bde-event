package com.example.bde_event.data.source

import com.example.bde_event.api.models.Event

interface RemoteDataSource {
    suspend fun getEvents(): List<Event>
    suspend fun addEvent(event: Event): Event
}
