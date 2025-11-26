package com.example.bde_event.data

import com.example.bde_event.Event
import com.example.bde_event.data.model.EventDto
import kotlinx.coroutines.delay

class EventRepository {

    // --- SIMULATION DE LA BDD ---
    private val fakeDatabase = mutableListOf(
        EventDto(
            id = 1,
            name = "Match de Basket",
            date = "2025-11-10T18:00:00",
            duration = "02:00",
            description = "Tournoi inter-promo",
            location = "Gymnase A",
            idType = 1,
            idUser = 10
        ),
        EventDto(
            id = 2,
            name = "Réunion BDE",
            date = "2025-11-10T20:30:00",
            duration = "01:00",
            description = "Préparation soirée",
            location = "Salle 101",
            idType = 2,
            idUser = 12
        )
    )

    // Récupérer les événements
    suspend fun getAllEvents(): List<Event> {
        delay(500) // Simulation réseau
        // BDD -> APP
        return fakeDatabase.map { it.toDomain() }
    }

    // Ajouter un événement
    suspend fun addEvent(event: Event): Boolean {
        delay(500) // Simulation réseau

        // 1. On transforme l'Event (App) en EventDto (BDD)
        val eventDto = event.toDto()

        // 2. On l'ajoute à la fausse liste (Simulation du POST)
        fakeDatabase.add(eventDto)

        return true
    }
}