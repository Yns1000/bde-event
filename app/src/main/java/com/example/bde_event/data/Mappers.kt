package com.example.bde_event.data

import android.annotation.SuppressLint
import com.example.bde_event.Event
import com.example.bde_event.data.model.EventDto
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Fonction pour convertir l'Event de l'API (Généré) vers ton Event UI
fun EventDto.toDomain(): Event {

    // La date arrive souvent sous forme de String depuis le généré
    val parsedDate = try {
        // Adapte selon ce que le générateur a produit (String ou Date)
        // Si c'est une String :
        if (this.date != null) LocalDateTime.parse(this.date, DateTimeFormatter.ISO_DATE_TIME) else LocalDateTime.now()
    } catch (e: Exception) {
        LocalDateTime.now()
    }

    // Gestion de la durée (String "02:00:00" -> Time)
    val durationTime = try {
        LocalTime.parse(this.duration ?: "01:00:00")
    } catch (e: Exception) {
        LocalTime.of(1, 0)
    }

    // Calcul de la fin
    val endDateTime = parsedDate
        .plusHours(durationTime.hour.toLong())
        .plusMinutes(durationTime.minute.toLong())

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val displayTime = "${parsedDate.format(timeFormatter)} - ${endDateTime.format(timeFormatter)}"

    val typeName = when(this.idType) {
        1 -> "Sport"
        2 -> "Réunion"
        3 -> "Culture"
        4 -> "Soirée"
        else -> "Autre"
    }

    return Event(
        id = this.id?.toInt() ?: 0, // Le générateur met souvent des Long ou null
        title = this.name ?: "Sans titre",
        startDate = parsedDate.toLocalDate(),
        endDate = endDateTime.toLocalDate(),
        time = displayTime,
        location = this.description, // On utilise description comme lieu si pas de champ lieu
        type = typeName,
        description = this.description
    )
}

// Fonction inverse : UI -> API (Généré)
@SuppressLint("DefaultLocale")
fun Event.toDto(): EventDto {
    // 1. On découpe ton affichage "14:00 - 16:00" pour retrouver les heures
    val timeParts = this.time.split(" - ")
    val startTimeStr = timeParts.getOrElse(0) { "00:00" }
    val endTimeStr = timeParts.getOrElse(1) { startTimeStr }

    // 2. On reconstruit la date format BDD (YYYY-MM-DDTHH:mm:ss)
    val fullDateIso = "${this.startDate}T$startTimeStr:00"

    // 3. On recalcule la DURÉE (fin - début)
    val start = LocalTime.parse(startTimeStr)
    val end = LocalTime.parse(endTimeStr)
    val durationObj = Duration.between(start, end)
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

    // Création de l'objet généré
    // Note : Les noms des paramètres dépendent de ce que le générateur a créé
    return EventDto(
        id = this.id.toLong(),
        name = this.title,
        date = fullDateIso,
        duration = durationStr, // 
        idType = typeId,
        idUser = 1, // Valeur par défaut
        description = this.description,
        location = this.location
    )
}