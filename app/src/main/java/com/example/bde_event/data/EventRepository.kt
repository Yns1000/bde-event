package com.example.bde_event.data

import com.example.bde_event.Event
import com.example.bde_event.data.model.EventDto
import kotlinx.coroutines.delay

class EventRepository {

    // --- SIMULATION DE LA BDD (Ce que l'API NestJS renverra) ---
    private val fakeDatabase = mutableListOf(
        EventDto(
            id = 1,
            name = "Match de Basket",
            date = "2025-11-10",
            duration = "02:00",
            description = "Tournoi inter-promo",
            location = "Gymnase A",
            idType = 1, // Sport
            idUser = 10
        ),
        EventDto(
            id = 2,
            name = "Réunion BDE",
            date = "2025-11-12",
            duration = "01:00",
            description = "Préparation soirée d'intégration",
            location = "Salle 101",
            idType = 2, // Réunion
            idUser = 12
        )
    )

    // Récupérer les événements
    suspend fun getAllEvents(): List<Event> {
        delay(500) // On simule un petit temps de chargement réseau
        // On transforme les DTOs (BDD) en Events (App) grâce au Mapper
        return fakeDatabase.map { it.toDomain() }
    }

    // Ajouter un événement
    suspend fun addEvent(event: Event): Boolean {
        delay(500)
        // Inversement : on devrait transformer l'Event en DTO pour l'envoyer
        // Ici on fait simple pour la simulation
        return true
    }
}