package com.example.bde_event.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    // États pour gérer l'affichage des calendriers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Toggle Bouton
        TextButton(
            onClick = onToggleFilters,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (filtersVisible) "Masquer les filtres" else "Afficher les filtres")
        }

        if (!filtersVisible) return@Column

        // 1. Recherche
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            placeholder = { Text("Rechercher titre, lieu...") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        // 2. Dates (Avec DatePicker)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Champ Date de début
            OutlinedTextField(
                value = startDateStr,
                onValueChange = {}, // On bloque l'écriture manuelle
                readOnly = true,    // Bloque le clavier et le stylet
                modifier = Modifier
                    .weight(1f)
                    .clickable { showStartDatePicker = true }, // Le clic ouvre le calendrier
                placeholder = { Text("Début") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Choisir date")
                    }
                }
            )

            // Champ Date de fin
            OutlinedTextField(
                value = endDateStr,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showEndDatePicker = true },
                placeholder = { Text("Fin") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Choisir date")
                    }
                }
            )
        }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
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

    // --- DIALOGUES CALENDRIER ---

    if (showStartDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { date ->
                onStartDateChanged(date ?: "")
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { date ->
                onEndDateChanged(date ?: "")
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

// Composant utilitaire pour le Calendrier
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDate = datePickerState.selectedDateMillis
                if (selectedDate != null) {
                    // Conversion du timestamp en format YYYY-MM-DD
                    val date = Instant.ofEpochMilli(selectedDate)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)
                    onDateSelected(date)
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}