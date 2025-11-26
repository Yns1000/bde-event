package com.example.bde_event.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onLogout: () -> Unit,
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {

    val viewModel: MainViewModel = viewModel()

    var isFilterDrawerOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    val categories = listOf("Tous", "Sport", "Réunion", "Culture", "Forum", "Tournoi", "Atelier", "Autre")
    val dummyDays = listOf("Tous")

    val isFilterActive = remember(viewModel.searchQuery, viewModel.selectedType, viewModel.startDateStr, viewModel.endDateStr) {
        viewModel.searchQuery.isNotBlank() ||
                viewModel.selectedType != "Tous" ||
                viewModel.startDateStr.isNotBlank() ||
                viewModel.endDateStr.isNotBlank()
    }

    if (viewModel.isAddingEvent) {

        AddEventScreen(
            onSave = { viewModel.addEvent(it) },
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

                // En-tête
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {

                        Image(
                            painter = painterResource(id = R.drawable.academia_icon),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp).padding(end = 8.dp)
                        )

                        Text(
                            text = "Événements",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        BadgedBox(
                            badge = {
                                if (isFilterActive) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error, modifier = Modifier.size(8.dp))
                                }
                            }
                        ) {
                            IconButton(onClick = { isFilterDrawerOpen = true }) {
                                Icon(Icons.Default.FilterList, "Filtres", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Basculer thème",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, "Déconnexion", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                HorizontalDivider()

                CategorySelector(
                    categories = categories,
                    selectedType = viewModel.selectedType,
                    onTypeSelected = { viewModel.selectedType = it }
                )

                WeeklySchedule(
                    week = viewModel.filteredEvents,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (isFilterDrawerOpen) {

        ModalBottomSheet(
            onDismissRequest = { isFilterDrawerOpen = false },
            sheetState = sheetState
        ) {

            FilterContent(
                days = dummyDays,
                selectedDay = "Tous",
                onDaySelected = {},

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
                    scope.launch {
                        sheetState.hide()
                        isFilterDrawerOpen = false
                    }
                }
            )
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<String>,
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(categories) { type ->

            val selected = type == selectedType

            FilterChip(
                selected = selected,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(type, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            )
        }
    }
}
