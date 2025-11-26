package com.example.bde_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bde_event.ui.components.FilterBarMobile
import com.example.bde_event.ui.components.WeeklySchedule
import com.example.bde_event.ui.theme.BdeeventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BdeeventTheme {
                // On récupère le ViewModel créé à l'étape 2
                val viewModel: MainViewModel = viewModel()

                // On prépare la structure de la page
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // Titre
                        Text(
                            text = "Événements de la semaine",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Divider()

                        // Barre de filtres (connectée au ViewModel)
                        FilterBarMobile(
                            days = listOf("Tous"), // Tu pourras rajouter les jours dynamiquement plus tard
                            types = listOf("Tous", "Sport", "Réunion", "Culture", "Forum", "Tournoi", "Atelier"),
                            query = viewModel.searchQuery,
                            onQueryChanged = { viewModel.searchQuery = it },
                            selectedDay = "Tous", // Simplification pour l'instant
                            onDaySelected = { },
                            startDateStr = viewModel.startDateStr,
                            onStartDateChanged = { viewModel.startDateStr = it },
                            endDateStr = viewModel.endDateStr,
                            onEndDateChanged = { viewModel.endDateStr = it },
                            selectedType = viewModel.selectedType,
                            onTypeSelected = { viewModel.selectedType = it },
                            filtersVisible = viewModel.filtersVisible, // Ajoute "var filtersVisible" dans ton ViewModel si ce n'est pas fait !
                            onToggleFilters = { viewModel.filtersVisible = !viewModel.filtersVisible }
                        )

                        // Liste des événements filtrés
                        WeeklySchedule(
                            week = viewModel.filteredEvents,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}