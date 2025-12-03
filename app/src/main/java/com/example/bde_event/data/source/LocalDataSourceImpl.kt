package com.example.bde_event.data.source

import com.example.bde_event.data.model.EventDto

class LocalDataSourceImpl : LocalDataSource {
    private val cachedEvents = mutableListOf<EventDto>()

    override suspend fun getEvents(): List<EventDto> {
        return cachedEvents
    }

    override suspend fun saveEvents(events: List<EventDto>) {
        cachedEvents.clear()
        cachedEvents.addAll(events)
    }

    override suspend fun addEvent(event: EventDto) {
        cachedEvents.add(event)
    }
}