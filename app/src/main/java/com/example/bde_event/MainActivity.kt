package com.example.bde_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.bde_event.ui.theme.BdeeventTheme
import java.time.LocalDate

// ✅ TA data class qui marchait
data class Event(
    val id: Int,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val time: String,
    val location: String?,
    val type: String
)

// ✅ TA map sampleWeek qui marchait
val sampleWeek: Map<String, List<Event>> = mapOf(
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BdeeventTheme {

                // ✅ États de TA version + toggle ajouté
                var selectedDay by remember { mutableStateOf("Tous") }
                var query by remember { mutableStateOf("") }
                var startDateStr by remember { mutableStateOf("") }
                var endDateStr by remember { mutableStateOf("") }
                var selectedType by remember { mutableStateOf("Tous") }
                var filtersVisible by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            // ✅ Titre
                            Text(
                                text = "Événements de la semaine",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Divider()

                            // ✅ Toggle ajouté
                            FilterBarMobile(
                                days = listOf("Tous") + sampleWeek.keys.toList(),
                                types = listOf("Tous", "Sport", "Réunion", "Culture", "Forum", "Tournoi", "Atelier"),
                                query = query,
                                onQueryChanged = { query = it },
                                selectedDay = selectedDay,
                                onDaySelected = { selectedDay = it },
                                startDateStr = startDateStr,
                                onStartDateChanged = { startDateStr = it },
                                endDateStr = endDateStr,
                                onEndDateChanged = { endDateStr = it },
                                selectedType = selectedType,
                                onTypeSelected = { selectedType = it },
                                filtersVisible = filtersVisible,
                                onToggleFilters = { filtersVisible = !filtersVisible }
                            )

                            // ✅ TA version call inchangée (moins selectedDay dans le UI mais toujours utile ici)
                            FilterableWeeklySchedule(
                                week = sampleWeek,
                                selectedDay = selectedDay,
                                query = query,
                                startDateStr = startDateStr,
                                endDateStr = endDateStr,
                                selectedType = selectedType,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBarMobile(
    days: List<String>,
    types: List<String>,
    query: String,
    onQueryChanged: (String) -> Unit,
    selectedDay: String,
    onDaySelected: (String) -> Unit,
    startDateStr: String,
    onStartDateChanged: (String) -> Unit,
    endDateStr: String,
    onEndDateChanged: (String) -> Unit,
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    filtersVisible: Boolean,
    onToggleFilters: () -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ✅ Toggle
        TextButton(
            onClick = onToggleFilters,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (filtersVisible) "Masquer les filtres" else "Afficher les filtres")
        }

        if (!filtersVisible) return@Column

        // ✅ Recherche arrondie
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            placeholder = { Text("Rechercher titre, lieu...") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        // ✅ Dates arrondies
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = startDateStr,
                onValueChange = onStartDateChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Début (YYYY-MM-DD)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = endDateStr,
                onValueChange = onEndDateChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Fin (YYYY-MM-DD)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // ✅ Type arrondi
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Type d'événement") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            onTypeSelected(type)
                            typeExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterableWeeklySchedule(
    week: Map<String, List<Event>>,
    selectedDay: String,
    query: String,
    startDateStr: String,
    endDateStr: String,
    selectedType: String,
    modifier: Modifier = Modifier
) {
    val startFilter = try { if (startDateStr.isBlank()) null else LocalDate.parse(startDateStr) } catch (_: Exception) { null }
    val endFilter = try { if (endDateStr.isBlank()) null else LocalDate.parse(endDateStr) } catch (_: Exception) { null }
    val lowerQuery = query.lowercase()

    val filtered = week.mapValues { (_, list) ->
        list.filter { ev ->
            val typeOk = selectedType == "Tous" || ev.type.equals(selectedType, true)
            val dateOk = when {
                startFilter == null && endFilter == null -> true
                startFilter != null && endFilter == null -> !ev.endDate.isBefore(startFilter)
                startFilter == null && endFilter != null -> !ev.startDate.isAfter(endFilter)
                else -> !(ev.endDate.isBefore(startFilter!!) || ev.startDate.isAfter(endFilter!!))
            }
            val textOk =
                lowerQuery.isEmpty() ||
                        ev.title.lowercase().contains(lowerQuery) ||
                        (ev.location?.lowercase()?.contains(lowerQuery) ?: false) ||
                        ev.time.lowercase().contains(lowerQuery)

            typeOk && dateOk && textOk
        }
    }

    WeeklySchedule(week = filtered, modifier = modifier)
}

@Composable
fun WeeklySchedule(week: Map<String, List<Event>>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(week.entries.toList()) { entry ->
            DaySection(entry.key, entry.value)
        }
    }
}

@Composable
fun DaySection(day: String, events: List<Event>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (events.isEmpty()) "Aucun événement" else "${events.size} év.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        if (events.isEmpty()) {
            Text(
                text = "Pas d'événements prévus",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                events.forEach {
                    EventCard(it)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = "${event.title} — ${event.type}",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "${event.startDate} ${event.time}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!event.location.isNullOrEmpty()) {
                    Text(event.location, style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    text = "Du ${event.startDate} au ${event.endDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
