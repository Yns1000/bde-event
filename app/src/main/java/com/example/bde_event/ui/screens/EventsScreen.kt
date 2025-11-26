package com.example.bde_event.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp // Si ça bug, met Icons.Default.ExitToApp
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

@Composable
fun     EventsScreen(onLogout: () -> Unit) {
    val viewModel: MainViewModel = viewModel()

    // 1. TA LOGIQUE : Si on ajoute un événement, on affiche le formulaire
    if (viewModel.isAddingEvent) {
        AddEventScreen(
            onSave = { event -> viewModel.addEvent(event) },
            onCancel = { viewModel.isAddingEvent = false }
        )
    } else {
        // 2. SINON : On affiche l'écran principal (Design de ton pote + Ton FAB)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            // TA LOGIQUE : Le bouton flottant pour ajouter
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
                // 3. LE DESIGN DE TON POTE : Titre + Bouton Déconnexion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Événements",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )

                    IconButton(onClick = { onLogout() }) {
                        // Attention : Icons.AutoMirrored nécessite une version récente de Compose
                        // Si ça souligne en rouge, remplace par Icons.Default.ExitToApp
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Se déconnecter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                HorizontalDivider()

                // 4. LES FILTRES (Ton pote a ajouté onClearFilters, on le garde)
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
                    onClearFilters = { viewModel.clearFilters() } // Si cette fonction n'existe pas dans ton ViewModel, retire cette ligne
                )

                // 5. LA LISTE
                WeeklySchedule(
                    week = viewModel.filteredEvents,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}