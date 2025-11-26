package com.example.bde_event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainViewModel : ViewModel() {

    // 1. On utilise un mutableStateMapOf pour que l'interface se mette à jour quand on ajoute un item
    private val _eventsMap = mutableStateMapOf<String, List<Event>>().apply {
        // On initialise avec tes données de base
        putAll(
            mapOf(
                "Lundi" to listOf(Event(1, "Entraînement Basket", LocalDate.of(2025,11,10), LocalDate.of(2025,11,10),"18:00 - 20:00","Gymnase A","Sport")),
                "Mardi" to listOf(Event(3, "Atelier Dév", LocalDate.of(2025,11,11), LocalDate.of(2025,11,11),"12:00 - 13:00","Salle 202","Atelier")),
                "Mercredi" to listOf(Event(4, "Concert Étudiant", LocalDate.of(2025,11,12), LocalDate.of(2025,11,12),"19:00 - 22:00","Amphi Central","Culture")),
                "Jeudi" to emptyList(),
                "Vendredi" to listOf(Event(5, "Forum IG2I", LocalDate.of(2025,11,14), LocalDate.of(2025,11,14),"09:00 - 17:00","Hall","Forum")),
                "Samedi" to listOf(Event(6, "Tournoi Interpromo", LocalDate.of(2025,11,15), LocalDate.of(2025,11,15),"10:00 - 16:00","Stade","Sport")),
                "Dimanche" to emptyList()
            )
        )
    }

    // 2. Variables d'état existantes (Filtres)
    var searchQuery by mutableStateOf("")
    var selectedType by mutableStateOf("Tous")
    var startDateStr by mutableStateOf("")
    var endDateStr by mutableStateOf("")
    var filtersVisible by mutableStateOf(false)
    var isAddingEvent by mutableStateOf(false)

    // NOUVELLE FONCTION : Réinitialise tous les filtres
    fun clearFilters() {
        searchQuery = ""
        selectedType = "Tous"
        startDateStr = ""
        endDateStr = ""
        // Note: filtersVisible n'est pas réinitialisé ici, car l'utilisateur veut souvent les garder visibles après un clear
    }
    // 4. Logique de filtrage (basée sur la map mutable)
    val filteredEvents: Map<String, List<Event>>
        get() {
            val startFilter = try { if (startDateStr.isBlank()) null else LocalDate.parse(startDateStr) } catch (_: Exception) { null }
            val endFilter = try { if (endDateStr.isBlank()) null else LocalDate.parse(endDateStr) } catch (_: Exception) { null }
            val lowerQuery = searchQuery.lowercase()

            return _eventsMap.mapValues { (_, list) ->
                list.filter { ev ->
                    val typeOk = selectedType == "Tous" || ev.type.equals(selectedType, true)
                    val dateOk = when {
                        startFilter == null && endFilter == null -> true
                        startFilter != null && endFilter == null -> !ev.endDate.isBefore(startFilter)
                        startFilter == null && endFilter != null -> !ev.startDate.isAfter(endFilter)
                        else -> !(ev.endDate.isBefore(startFilter!!) || ev.startDate.isAfter(endFilter!!))
                    }
                    val textOk = lowerQuery.isEmpty() ||
                            ev.title.lowercase().contains(lowerQuery) ||
                            (ev.location?.lowercase()?.contains(lowerQuery) ?: false)

                    typeOk && dateOk && textOk
                }
            }
        }

    // 5. NOUVELLE FONCTION : Ajouter un événement
    fun addEvent(newEvent: Event) {
        // On trouve le jour de la semaine en français (ex: "Lundi")
        val dayName = newEvent.startDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // On récupère la liste actuelle pour ce jour, on ajoute, et on met à jour la map
        val currentList = _eventsMap[dayName] ?: emptyList()
        _eventsMap[dayName] = currentList + newEvent

        // On revient à l'écran principal
        isAddingEvent = false
    }
}