// kotlin
package com.example.bde_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bde_event.ui.theme.BdeeventTheme

val sampleWeek: Map<String, List<Event>> = mapOf(
    "Lundi" to listOf(
        Event(1, "Entraînement Basket", "18:00 - 20:00", "Gymnase A"),
        Event(2, "Réunion BDE", "20:30 - 21:30", "Salle 101")
    ),
    "Mardi" to listOf(
        Event(3, "Atelier Dév", "12:00 - 13:00", "Salle 202")
    ),
    "Mercredi" to listOf(
        Event(4, "Concert Étudiant", "19:00 - 22:00", "Amphi Central")
    ),
    "Jeudi" to emptyList(),
    "Vendredi" to listOf(
        Event(5, "Forum IG2I", "09:00 - 17:00", "Hall")
    ),
    "Samedi" to listOf(
        Event(6, "Tournoi Interpromo", "10:00 - 16:00", "Stade")
    ),
    "Dimanche" to emptyList()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BdeeventTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        WeeklySchedule(week = sampleWeek, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklySchedule(week: Map<String, List<Event>>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(week.entries.toList()) { (day, events) ->
            DaySection(day = day, events = events)
        }
    }
}

@Composable
fun DaySection(day: String, events: List<Event>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
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
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End
            )
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        if (events.isEmpty()) {
            Text(
                text = "Pas d'événements prévus",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                events.forEach { ev ->
                    EventCard(ev)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Column {
                Text(text = event.title, style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = event.time, style = MaterialTheme.typography.bodySmall)
                    if (!event.location.isNullOrEmpty()) {
                        Text(text = event.location, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeeklySchedulePreview() {
    BdeeventTheme {
        Surface {
            WeeklySchedule(week = sampleWeek, modifier = Modifier.fillMaxSize().padding(12.dp))
        }
    }
}
