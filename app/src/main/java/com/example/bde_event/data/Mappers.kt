package com.example.bde_event.data

import android.annotation.SuppressLint
import com.example.bde_event.Event
import com.example.bde_event.data.model.EventDto
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun EventDto.toDomain(): Event {
    // 1. On parse la Date de Début (qui inclut l'heure maintenant !)
    // On essaie de parser un format ISO (ex: 2025-11-26T14:00:00)
    val startDateTime = try {
        LocalDateTime.parse(this.date) // Parse le format par défaut ISO-8601
    } catch (e: Exception) {
        // Si ça échoue (format SQL classique avec espace), on essaie un autre format
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(this.date, formatter)
        } catch (e2: Exception) {
            LocalDateTime.now() // Secours
        }
    }

    // 2. On parse la Durée (ex: "02:00")
    val durationTime = try {
        LocalTime.parse(this.duration)
    } catch (e: Exception) {
        LocalTime.of(1, 0) // 1h par défaut
    }

    // 3. On CALCULE la fin
    val endDateTime = startDateTime
        .plusHours(durationTime.hour.toLong())
        .plusMinutes(durationTime.minute.toLong())

    // 4. On formate l'affichage "HH:mm - HH:mm"
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val displayTime = "${startDateTime.format(timeFormatter)} - ${endDateTime.format(timeFormatter)}"

    // 5. Gestion du Type (ton mock)
    val typeName = when(this.idType) {
        1 -> "Sport"
        2 -> "Réunion"
        3 -> "Culture"
        4 -> "Soirée"
        else -> "Autre"
    }

    return Event(
        id = this.id,
        title = this.name,
        startDate = startDateTime.toLocalDate(), // On extrait juste la date pour le tri
        endDate = endDateTime.toLocalDate(),     // Si ça finit le lendemain, on le saura !
        time = displayTime,                      // "14:00 - 16:00" généré automatiquement
        location = this.location ?: "Lieu à définir",
        type = typeName,
        description = this.description
    )
}

@SuppressLint("DefaultLocale")
fun Event.toDto(): EventDto {
    // 1. On découpe ton affichage "14:00 - 16:00" pour retrouver les heures
    val timeParts = this.time.split(" - ")
    val startTimeStr = timeParts.getOrElse(0) { "00:00" }
    val endTimeStr = timeParts.getOrElse(1) { startTimeStr }

    // 2. On reconstruit la date format BDD (YYYY-MM-DDTHH:mm:ss)
    val fullDateIso = "${this.startDate}T$startTimeStr:00"

    // 3. On recalcule la DURÉE (fin - début)
    val start = java.time.LocalTime.parse(startTimeStr)
    val end = java.time.LocalTime.parse(endTimeStr)
    val durationObj = java.time.Duration.between(start, end)
    // Format "HH:mm" (ex: 02:00)
    val durationStr = String.format("%02d:%02d", durationObj.toHours(), durationObj.toMinutes() % 60)

    // 4. Conversion Type (Nom -> ID)
    val typeId = when(this.type) {
        "Sport" -> 1
        "Réunion" -> 2
        "Culture" -> 3
        "Soirée" -> 4
        else -> 5
    }

    return EventDto(
        id = this.id,
        name = this.title,
        date = fullDateIso,
        duration = durationStr,
        description = this.description,
        location = this.location,
        idType = typeId,
        idUser = 1 // ID utilisateur par défaut
    )
}