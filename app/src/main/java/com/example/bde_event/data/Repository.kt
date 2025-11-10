package com.example.bde_event.data

import com.example.bde_event.data.dao.EventDao
import com.example.bde_event.data.entities.EventEntity
import com.example.bde_event.data.entities.TypeOfEventEntity
import kotlinx.coroutines.flow.Flow

class Repository(private val dao: EventDao) {
    fun getEventsFlow(showPast: Boolean, query: String?): Flow<List<EventEntity>> {
        val minDate = if (showPast) null else System.currentTimeMillis()
        val q = if (query.isNullOrBlank()) null else "%${query.trim()}%"
        return dao.getEventsFlow(minDate, q)
    }

    fun getTypesFlow(): Flow<List<TypeOfEventEntity>> = dao.getTypesFlow()

    suspend fun insertEvent(event: EventEntity) = dao.insert(event)
}
