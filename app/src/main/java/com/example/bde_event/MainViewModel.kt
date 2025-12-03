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

    // 2. On stocke la liste complète reçue du "server"
    var allEvents by mutableStateOf<List<Event>>(emptyList())

    // Variables d'état pour les filtres (RÉTABLIES)
    var searchQuery by mutableStateOf("")
    var selectedType by mutableStateOf("Tous")
    var startDateStr by mutableStateOf("") // Rétabli
    var endDateStr by mutableStateOf("")   // Rétabli
    var filtersVisible by mutableStateOf(false)
    var isAddingEvent by mutableStateOf(false)

    // Remarque : selectedDay n'est plus nécessaire ici pour l'affichage, mais il était présent.
    // Je le retire pour simplifier, car le carrousel de jour n'est plus là.

    // Au lancement, on charge les données
    init {
        loadEvents()
    }

    // Fonction pour charger les événements depuis le Repository
    private fun loadEvents() {
        viewModelScope.launch {
            allEvents = repository.getAllEvents()
        }
    }

    // Réinitialiser les filtres (Rétablie)
    fun clearFilters() {
        searchQuery = ""
        selectedType = "Tous"
        startDateStr = ""
        endDateStr = ""
    }

    // Logique utilitaire matchesDay supprimée (n'est plus utilisée).

    // 3. Logique de filtrage ET de regroupement (Rétablie)
    val filteredEvents: Map<String, List<Event>>
        get() {
            // A. Préparation des filtres
            val startFilter = try { if (startDateStr.isBlank()) null else LocalDate.parse(startDateStr) } catch (_: Exception) { null }
            val endFilter = try { if (endDateStr.isBlank()) null else LocalDate.parse(endDateStr) } catch (_: Exception) { null }
            val lowerQuery = searchQuery.lowercase()

            // B. Filtrage de la liste brute
            val filteredList = allEvents.filter { ev ->
                // Filtre 1 : Par Type
                val typeOk = selectedType == "Tous" || ev.type.equals(selectedType, true)

                // Filtre 2 : Par Plage de dates (pour le ModalBottomSheet)
                val dateRangeOk = when {
                    startFilter == null && endFilter == null -> true
                    startFilter != null && endFilter == null -> !ev.endDate.isBefore(startFilter)
                    startFilter == null && endFilter != null -> !ev.startDate.isAfter(endFilter)
                    else -> !(ev.endDate.isBefore(startFilter!!) || ev.startDate.isAfter(endFilter!!))
                }

                // Filtre 3 : Par Texte
                val textOk = lowerQuery.isEmpty() ||
                        ev.title.lowercase().contains(lowerQuery) ||
                        (ev.location?.lowercase()?.contains(lowerQuery) ?: false)

                // Tous les filtres doivent être validés
                typeOk && dateRangeOk && textOk
            }

            // C. Regroupement par jour de la semaine (Ex: "Lundi", "Mardi")
            return filteredList
                .sortedBy { it.startDate }
                .groupBy { event ->
                    // On utilise le nom complet du jour pour le regroupement
                    event.startDate.dayOfWeek
                        .getDisplayName(TextStyle.FULL, Locale.FRENCH)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
        }

    // 4. Ajouter un événement (via le Repository)
    fun addEvent(newEvent: Event) {
        viewModelScope.launch {
            val success = repository.addEvent(newEvent)

            if (success) {
                loadEvents()
                isAddingEvent = false
            }
        }
    }
}