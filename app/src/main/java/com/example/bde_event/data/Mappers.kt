package com.example.bde_event.data

import com.example.bde_event.Event
import com.example.bde_event.data.model.EventDto
import java.time.LocalDate

fun EventDto.toDomain(): Event {
    // 1. On sécurise la date (au cas où l'API envoie un format bizarre)
    val parsedDate = try {
        LocalDate.parse(this.date)
    } catch (e: Exception) {
        LocalDate.now()
    }

    // 2. On convertit l'ID du type en vrai nom (en attendant que l'API le fasse)
    val typeName = when(this.idType) {
        1 -> "Sport"
        2 -> "Réunion"
        3 -> "Culture"
        4 -> "Soirée"
        else -> "Autre"
    }

    // 3. On crée l'objet Event pour l'affichage
    return Event(
        id = this.id,
        title = this.name,           // BDD 'name' -> App 'title'
        startDate = parsedDate,
        endDate = parsedDate,        // On met la même date par défaut
        time = this.duration,        // On affiche la durée (ex: "02:00")
        location = this.location ?: "Lieu à définir",
        type = typeName
    )
}