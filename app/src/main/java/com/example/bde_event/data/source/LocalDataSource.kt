package com.example.bde_event.data.source

import com.example.bde_event.data.model.EventDto

interface LocalDataSource {
    suspend fun getEvents(): List<EventDto>
    suspend fun saveEvents(events: List<EventDto>)
    suspend fun addEvent(event: EventDto)
}