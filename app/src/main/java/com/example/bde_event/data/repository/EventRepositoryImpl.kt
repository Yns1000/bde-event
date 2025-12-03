// kotlin
package com.example.bde_event.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.bde_event.Event
import com.example.bde_event.api.models.Event as ApiEvent
import com.example.bde_event.data.model.EventDto
import com.example.bde_event.data.source.LocalDataSource
import com.example.bde_event.data.source.RemoteDataSource
import com.example.bde_event.data.toDomain
import com.example.bde_event.data.toDto
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.time.format.DateTimeFormatter
import kotlin.text.format

class EventRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : EventRepository {

    override suspend fun getEvents(): List<Event> {
        try {
            val remoteEvents = remoteDataSource.getEvents()

            val eventDtos = remoteEvents.map { apiEvent ->
                EventDto(
                    id = apiEvent.id ?: 0L,
                    name = apiEvent.name ?: "",
                    date = apiEvent.date?.format(DateTimeFormatter.ISO_DATE) ?: "",
                    duration = apiEvent.duration ?: "",
                    description = apiEvent.description,
                    location = null,
                    idType = apiEvent.idType ?: 0,
                    idUser = apiEvent.idUser ?: 0
                )
            }

            localDataSource.saveEvents(eventDtos)
        } catch (e: Exception) {
            // Erreur réseau
            Log.e("EventRepositoryImpl", "Erreur lors de la récupération des événements distants: ${e.message}")
        }

        return localDataSource.getEvents().map { it.toDomain() }
    }

    override suspend fun addEvent(event: Event) {
        val eventDto = event.toDto()

        // 1. Ajouter en local
        localDataSource.addEvent(eventDto)

        try {
            val parsedDate = parseToLocalDate(eventDto.date) ?: LocalDate.now()

            val apiEvent = ApiEvent(
                id = eventDto.id,
                name = eventDto.name,
                date = parsedDate,
                duration = eventDto.duration,
                description = eventDto.description,
                lieu = eventDto.location,
                idType = eventDto.idType,
                idUser = eventDto.idUser
            )

            // 2. Envoyer à l'API
            remoteDataSource.addEvent(apiEvent)

            // 3. Resynchroniser depuis l'API
            val remoteEvents = remoteDataSource.getEvents()
            val eventDtos = remoteEvents.map { apiEv ->
                EventDto(
                    id = apiEv.id ?: 0L,
                    name = apiEv.name ?: "",
                    date = apiEv.date?.format(DateTimeFormatter.ISO_DATE) ?: "",
                    duration = apiEv.duration ?: "",
                    description = apiEv.description,
                    location = apiEv.lieu,
                    idType = apiEv.idType ?: 0,
                    idUser = apiEv.idUser ?: 0
                )
            }
            localDataSource.saveEvents(eventDtos)

        } catch (e: Exception) {
            Log.e("EventRepositoryImpl", "Erreur lors de l'ajout distant: ${e.message}", e)
            throw IOException("Erreur lors de la création de l'événement", e)
        }
    }


    private fun parseToLocalDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null

        // Essayer plusieurs parseurs : LocalDate, OffsetDateTime, LocalDateTime
        try {
            return LocalDate.parse(dateStr) // "yyyy-MM-dd"
        } catch (ignored: DateTimeParseException) { }

        try {
            // ex: "2025-12-03T08:17:05" -> OffsetDateTime ou LocalDateTime
            val odt = OffsetDateTime.parse(dateStr)
            return odt.toLocalDate()
        } catch (ignored: DateTimeParseException) { }

        try {
            val ldt = LocalDateTime.parse(dateStr)
            return ldt.toLocalDate()
        } catch (ignored: DateTimeParseException) { }

        // Dernier recours : tenter d'extraire la date prefix "yyyy-MM-dd"
        return try {
            if (dateStr.length >= 10) LocalDate.parse(dateStr.substring(0, 10)) else null
        } catch (ignored: Exception) {
            null
        }
    }
}
