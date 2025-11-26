package com.example.bde_event.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bde_event.MainViewModel
import com.example.bde_event.R
import com.example.bde_event.ui.components.AddEventScreen
import com.example.bde_event.ui.components.FilterContent
import com.example.bde_event.ui.components.WeeklySchedule
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(onLogout: () -> Unit) {
    val viewModel: MainViewModel = viewModel()

    // --- États et Logique UI ---
    var isFilterDrawerOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    // Liste des catégories pour le sélecteur et le filtre
    val categories = listOf("Tous", "Sport", "Réunion", "Culture", "Forum", "Tournoi", "Atelier", "Autre")

    // Pour l'appel complet au FilterContent, nous avons besoin de jours factices
    val dummyDays = listOf("Tous")

    // Détermination de l'état du filtre (pour le badge)
    val isFilterActive = remember(viewModel.searchQuery, viewModel.selectedType, viewModel.startDateStr, viewModel.endDateStr) {
        viewModel.searchQuery.isNotBlank() ||
                viewModel.selectedType != "Tous" ||
                viewModel.startDateStr.isNotBlank() ||
                viewModel.endDateStr.isNotBlank()
    }

    if (viewModel.isAddingEvent) {
        AddEventScreen(
            onSave = { event -> viewModel.addEvent(event) },
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- LIGNE D'EN-TÊTE ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo et Titre
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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

                    // Actions (Filtre + Déconnexion)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Bouton de filtre avec badge si filtre actif
                        BadgedBox(
                            badge = {
                                if (isFilterActive) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error, modifier = Modifier.size(8.dp))
                                }
                            }
                        ) {
                            IconButton(onClick = { isFilterDrawerOpen = true }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Ouvrir les filtres",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Bouton de déconnexion
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Se déconnecter",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                HorizontalDivider()

                // --- SÉLECTEUR DE CATÉGORIES (Carrousel) ---
                CategorySelector(
                    categories = categories,
                    selectedType = viewModel.selectedType,
                    onTypeSelected = { viewModel.selectedType = it }
                )

                // 5. LA LISTE
                WeeklySchedule(
                    week = viewModel.filteredEvents,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // 3. AFFICHAGE DU TIROIR DE FILTRE EN MODAL BOTTOM SHEET
    if (isFilterDrawerOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                isFilterDrawerOpen = false
            },
            sheetState = sheetState
        ) {
            // APPEL CORRIGÉ ET COMPLET DE FILTERCONTENT
            FilterContent(
                // Arguments requis par la signature complète (même si non utilisés dans le VM pour le filtre de jour)
                days = dummyDays,
                selectedDay = "Tous",
                onDaySelected = { /* No-op */ },

                // Arguments réels
                types = categories,
                query = viewModel.searchQuery,
                onQueryChanged = { viewModel.searchQuery = it },
                selectedType = viewModel.selectedType,
                onTypeSelected = { viewModel.selectedType = it },
                startDateStr = viewModel.startDateStr,
                onStartDateChanged = { viewModel.startDateStr = it },
                endDateStr = viewModel.endDateStr,
                onEndDateChanged = { viewModel.endDateStr = it },

                onClearFilters = { viewModel.clearFilters() },
                onCloseDrawer = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            isFilterDrawerOpen = false
                        }
                    }
                }
            )
        }
    }
}

// --- COMPOSANT : SÉLECTEUR DE CATÉGORIES ---
@Composable
fun CategorySelector(
    categories: List<String>,
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(categories) { type ->
            val isSelected = type == selectedType

            FilterChip(
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        text = type,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = if (isSelected) null else BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}