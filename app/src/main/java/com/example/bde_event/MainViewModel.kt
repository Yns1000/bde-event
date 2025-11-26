package com.example.bde_event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bde_event.data.EventRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainViewModel : ViewModel() {

    // 1. On connecte le Repository (notre source de données)
    private val repository = EventRepository()

    // 2. On stocke la liste complète reçue du "serveur" (Source de vérité)
    private var allEvents by mutableStateOf<List<Event>>(emptyList())

    // Variables d'état pour les filtres (inchangées)
    var searchQuery by mutableStateOf("")
    var selectedType by mutableStateOf("Tous")
    var startDateStr by mutableStateOf("")
    var endDateStr by mutableStateOf("")
    var filtersVisible by mutableStateOf(false)
    var isAddingEvent by mutableStateOf(false)

    // Au lancement, on charge les données
    init {
        loadEvents()
    }

    // Fonction pour charger les événements depuis le Repository
    private fun loadEvents() {
        viewModelScope.launch {
            // C'est ici que la magie opère : ça récupère les données simulées (et bientôt l'API)
            allEvents = repository.getAllEvents()
        }
    }

    // Réinitialiser les filtres
    fun clearFilters() {
        searchQuery = ""
        selectedType = "Tous"
        startDateStr = ""
        endDateStr = ""
    }

    // 3. Logique de filtrage ET de regroupement
    // L'écran attend une Map<String, List<Event>>, donc on transforme la liste ici
    val filteredEvents: Map<String, List<Event>>
        get() {
            // A. Préparation des filtres
            val startFilter = try { if (startDateStr.isBlank()) null else LocalDate.parse(startDateStr) } catch (_: Exception) { null }
            val endFilter = try { if (endDateStr.isBlank()) null else LocalDate.parse(endDateStr) } catch (_: Exception) { null }
            val lowerQuery = searchQuery.lowercase()

            // B. Filtrage de la liste brute
            val filteredList = allEvents.filter { ev ->
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

            // C. Regroupement par jour de la semaine (Ex: "Lundi", "Mardi")
            // On trie d'abord par date pour que l'ordre soit correct (Lundi avant Mardi)
            return filteredList
                .sortedBy { it.startDate }
                .groupBy { event ->
                    // On transforme la date en nom de jour (ex: 2025-11-10 -> "Lundi")
                    event.startDate.dayOfWeek
                        .getDisplayName(TextStyle.FULL, Locale.FRENCH)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
        }

    // 4. Ajouter un événement (via le Repository)
    fun addEvent(newEvent: Event) {
        viewModelScope.launch {
            // On demande au repo d'ajouter l'événement
            val success = repository.addEvent(newEvent)

            if (success) {
                // Si ça a marché, on recharge la liste pour voir le nouvel événement
                loadEvents()
                isAddingEvent = false
            }
        }
    }
}