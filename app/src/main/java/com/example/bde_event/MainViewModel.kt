package com.example.bde_event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class MainViewModel : ViewModel() {

    // ✅ 1. ON DÉFINIT LES DONNÉES ICI (C'est le "sampleWeek" qui manquait)
    private val sampleWeek: Map<String, List<Event>> = mapOf(
        "Lundi" to listOf(
            Event(1, "Entraînnement Basket", LocalDate.of(2025,11,10), LocalDate.of(2025,11,10),"18:00 - 20:00","Gymnase A","Sport"),
            Event(2, "Réunion BDE", LocalDate.of(2025,11,10), LocalDate.of(2025,11,10),"20:30 - 21:30","Salle 101","Réunion")
        ),
        "Mardi" to listOf(
            Event(3, "Atelier Dév", LocalDate.of(2025,11,11), LocalDate.of(2025,11,11),"12:00 - 13:00","Salle 202","Atelier")
        ),
        "Mercredi" to listOf(
            Event(4, "Concert Étudiant", LocalDate.of(2025,11,12), LocalDate.of(2025,11,12),"19:00 - 22:00","Amphi Central","Culture")
        ),
        "Jeudi" to emptyList(),
        "Vendredi" to listOf(
            Event(5, "Forum IG2I", LocalDate.of(2025,11,14), LocalDate.of(2025,11,14),"09:00 - 17:00","Hall","Forum")
        ),
        "Samedi" to listOf(
            Event(6, "Tournoi Interpromo", LocalDate.of(2025,11,15), LocalDate.of(2025,11,15),"10:00 - 16:00","Stade","Sport")
        ),
        "Dimanche" to emptyList()
    )

    // ✅ 2. VARIABLES D'ÉTAT (Pour les filtres)
    var searchQuery by mutableStateOf("")
    var selectedType by mutableStateOf("Tous")
    var startDateStr by mutableStateOf("")
    var endDateStr by mutableStateOf("")
    var filtersVisible by mutableStateOf(false)

    // ✅ 3. LOGIQUE DE FILTRAGE
    val filteredEvents: Map<String, List<Event>>
        get() {
            // Conversion sécurisée des dates
            val startFilter = try { if (startDateStr.isBlank()) null else LocalDate.parse(startDateStr) } catch (_: Exception) { null }
            val endFilter = try { if (endDateStr.isBlank()) null else LocalDate.parse(endDateStr) } catch (_: Exception) { null }
            val lowerQuery = searchQuery.lowercase()

            // On parcourt la map "sampleWeek" définie juste au-dessus
            return sampleWeek.mapValues { (_, list) ->
                list.filter { ev ->
                    // Filtre par Type
                    val typeOk = selectedType == "Tous" || ev.type.equals(selectedType, true)

                    // Filtre par Date
                    val dateOk = when {
                        startFilter == null && endFilter == null -> true
                        startFilter != null && endFilter == null -> !ev.endDate.isBefore(startFilter)
                        startFilter == null && endFilter != null -> !ev.startDate.isAfter(endFilter)
                        else -> !(ev.endDate.isBefore(startFilter!!) || ev.startDate.isAfter(endFilter!!))
                    }

                    // Filtre par Texte (Titre ou Lieu)
                    // C'est ici que tu avais l'erreur 'title'/'location' car 'ev' n'était pas reconnu
                    val textOk = lowerQuery.isEmpty() ||
                            ev.title.lowercase().contains(lowerQuery) ||
                            (ev.location?.lowercase()?.contains(lowerQuery) ?: false)

                    typeOk && dateOk && textOk
                }
            }
        }
}