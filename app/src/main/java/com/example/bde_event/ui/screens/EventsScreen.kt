// kotlin
// File: app/src/main/java/com/example/bde_event/ui/screens/EventsScreen.kt
package com.example.bde_event.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bde_event.MainViewModel
import com.example.bde_event.ui.components.AddEventScreen
import com.example.bde_event.ui.components.FilterBarMobile
import com.example.bde_event.ui.components.WeeklySchedule
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.bde_event.R

@Composable
fun EventsScreen(onLogout: () -> Unit) {
    val viewModel: MainViewModel = viewModel()

    if (viewModel.isAddingEvent) {
        AddEventScreen(
            types = viewModel.types, // on utilise la liste réellement chargée
            onSave = { event, typeId ->
                // Appel de la nouvelle méthode qui prend en compte l'id du type
                viewModel.addEvent(event, typeId)
            },
            onCancel = { viewModel.isAddingEvent = false }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.isAddingEvent = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter événement")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.academia_icon),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        )

                        Text(
                            text = "Événements",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Se déconnecter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                HorizontalDivider()

                FilterBarMobile(
                    days = listOf("Tous"),
                    types = listOf("Tous", "Sport", "Réunion", "Culture", "Forum", "Tournoi", "Atelier"),
                    query = viewModel.searchQuery,
                    onQueryChanged = { viewModel.searchQuery = it },
                    selectedDay = "Tous",
                    onDaySelected = { },
                    startDateStr = viewModel.startDateStr,
                    onStartDateChanged = { viewModel.startDateStr = it },
                    endDateStr = viewModel.endDateStr,
                    onEndDateChanged = { viewModel.endDateStr = it },
                    selectedType = viewModel.selectedType,
                    onTypeSelected = { viewModel.selectedType = it },
                    filtersVisible = viewModel.filtersVisible,
                    onToggleFilters = { viewModel.filtersVisible = !viewModel.filtersVisible },
                    onClearFilters = { viewModel.clearFilters() }
                )

                WeeklySchedule(
                    week = viewModel.filteredEvents,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
