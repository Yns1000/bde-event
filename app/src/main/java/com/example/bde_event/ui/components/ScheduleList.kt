package com.example.bde_event.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bde_event.Event

@Composable
fun WeeklySchedule(
    week: Map<String, List<Event>>,
    modifier: Modifier = Modifier
) {
    if (week.values.all { it.isEmpty() }) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aucun événement trouvé", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(week.entries.toList()) { entry ->
                // On affiche la section seulement s'il y a des événements (optionnel)
                // ou on garde ton comportement actuel :
                DaySection(entry.key, entry.value)
            }
        }
    }
}

@Composable
fun DaySection(day: String, events: List<Event>) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                text = if (events.isEmpty()) "Aucun" else "${events.size}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        if (events.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                events.forEach { event ->
                    EventCard(event) // On utilise le composant qu'on a créé au point 3
                }
            }
        }
    }
}