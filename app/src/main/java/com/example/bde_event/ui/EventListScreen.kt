package com.example.bde_event.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bde_event.Event
import com.example.bde_event.EventItemComposable
import com.example.bde_event.data.entities.EventEntity

@Composable
fun EventListScreen(
    viewModel: MainViewModel,
    onSubscribe: () -> Unit,
    onAdmin: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val isAuthor by viewModel.isAuthor.collectAsState()

    var query by rememberSaveable { mutableStateOf("") }
    var showPast by rememberSaveable { mutableStateOf(false) }

    // When UI states change, propagate to ViewModel
    LaunchedEffect(query) { viewModel.setQuery(query) }
    LaunchedEffect(showPast) { viewModel.setShowPast(showPast) }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onSubscribe) {
                Text(text = "S'abonner à la newsletter")
            }

            Row {
                if (!isAuthor) {
                    Button(onClick = { viewModel.loginAsAuthor() }, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Connexion")
                    }
                } else {
                    Button(onClick = { viewModel.logout() }, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Déconnexion")
                    }
                    Button(onClick = onAdmin, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Espace administrateur")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Rechercher titre / description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Afficher les événements passés", modifier = Modifier.alignByBaseline())
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = showPast, onCheckedChange = { showPast = it })
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(events) { eventEntity ->
                EventItemComposable(event = eventEntity.toDomain(), onClick = { /* open detail */ })
                HorizontalDivider()
            }
        }
    }
}

// Mapper function to convert EventEntity to Event (for UI compatibility)
private fun EventEntity.toDomain() = Event(
    id = id,
    name = name,
    idType = idType,
    idUser = idUser,
    dateMillis = dateMillis,
    durationMinutes = durationMinutes,
    description = description
)
