package com.example.bde_event

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bde_event.api.models.Event as ApiEvent
import com.example.bde_event.data.ApiClient
import com.example.bde_event.data.toDto
import com.example.bde_event.data.model.EventDto
import com.example.bde_event.data.model.TypeOfEventDto
import com.example.bde_event.data.repository.EventRepository
import com.example.bde_event.data.repository.EventRepositoryImpl
import com.example.bde_event.data.source.LocalDataSourceImpl
import com.example.bde_event.data.source.RemoteDataSourceImpl
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainViewModel : ViewModel() {

    private val apiService = ApiClient.eventApi

    private val remoteDataSource = RemoteDataSourceImpl(apiService)
    private val localDataSource = LocalDataSourceImpl()

    private val repository: EventRepository = EventRepositoryImpl(remoteDataSource, localDataSource)

    private var allEvents by mutableStateOf<List<Event>>(emptyList())

    var types by mutableStateOf<List<TypeOfEventDto>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
    var selectedType by mutableStateOf("Tous")
    var startDateStr by mutableStateOf("")
    var endDateStr by mutableStateOf("")
    var filtersVisible by mutableStateOf(false)
    var isAddingEvent by mutableStateOf(false)

    init {
        loadTypes()
        loadEvents()
    }

    private fun loadTypes() {
        viewModelScope.launch {
            try {
                types = ApiClient.apiService.getTypes()
            } catch (e: Exception) {
                Log.w("MainViewModel", "Impossible de charger les types d'événements: ${e.message}")
                types = emptyList()
            }
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            allEvents = repository.getEvents()
        }
    }

    fun addEvent(newEvent: Event) {
        addEvent(newEvent, 0)
    }

    fun addEvent(newEvent: Event, typeId: Int) {
        viewModelScope.launch {
            try {
                val dto = newEvent.toDto().copy(idType = typeId)

                localDataSource.addEvent(dto)

                val parsedDate = try {
                    LocalDate.parse(dto.date)
                } catch (_: Exception) {
                    LocalDate.now()
                }

                val apiEvent = ApiEvent(
                    id = dto.id,
                    name = dto.name,
                    idType = dto.idType,
                    idUser = dto.idUser,
                    date = parsedDate,
                    duration = dto.duration,
                    description = dto.description,
                    lieu = newEvent.location ?: ""
                )

                try {
                    remoteDataSource.addEvent(apiEvent)
                    Log.d("MainViewModel", "Événement envoyé au serveur avec succès")

                    val remoteEvents = remoteDataSource.getEvents()
                    val eventDtos = remoteEvents.map { event ->
                        EventDto(
                            id = event.id ?: 0L,
                            name = event.name ?: "",
                            date = event.date?.format(DateTimeFormatter.ISO_DATE) ?: "",
                            duration = event.duration ?: "",
                            description = event.description,
                            location = event.lieu,
                            idType = event.idType ?: 0,
                            idUser = event.idUser ?: 0
                        )
                    }
                    localDataSource.saveEvents(eventDtos)

                } catch (e: Exception) {
                    Log.w("MainViewModel", "Échec envoi distant : ${e.message}")
                }

                loadEvents()
                isAddingEvent = false

            } catch (e: Exception) {
                Log.e("MainViewModel", "Erreur lors de l'ajout d'événement : ${e.message}", e)
                isAddingEvent = false
            }
        }
    }

    fun clearFilters() {
        searchQuery = ""
        selectedType = "Tous"
        startDateStr = ""
        endDateStr = ""
    }

    val filteredEvents: Map<String, List<Event>>
        get() {
            val startFilter = try { if (startDateStr.isBlank()) null else LocalDate.parse(startDateStr) } catch (_: Exception) { null }
            val endFilter = try { if (endDateStr.isBlank()) null else LocalDate.parse(endDateStr) } catch (_: Exception) { null }
            val lowerQuery = searchQuery.lowercase()

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
                        (ev.description?.lowercase()?.contains(lowerQuery) == true)

                typeOk && dateOk && textOk
            }

            return filteredList
                .sortedBy { it.startDate }
                .groupBy { event ->
                    event.startDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)
                        .replaceFirstChar { it.uppercase() }
                }
        }
}
