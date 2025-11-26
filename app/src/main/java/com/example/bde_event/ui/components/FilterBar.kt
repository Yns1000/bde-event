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
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset // Import pour la correction
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.concurrent.TimeUnit

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
    onToggleFilters: () -> Unit,
    onClearFilters: () -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }

    // États pour gérer l'affichage des calendriers
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // DÉTERMINER SI UN FILTRE EST ACTIF
    val isFilterActive = remember(query, selectedType, startDateStr, endDateStr) {
        query.isNotBlank() || selectedType != "Tous" || startDateStr.isNotBlank() || endDateStr.isNotBlank()
    }

    // FONCTION UTILITAIRE POUR CONVERTIR "AAAA-MM-JJ" EN MILLISECONDES (CORRIGÉE)
    fun dateToMillis(dateStr: String): Long? {
        if (dateStr.isBlank()) return null
        return try {
            // Utilise l'offset UTC pour garantir que la date sélectionnée est à minuit UTC
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC) // CORRECTION APPLIQUÉE ICI
                .toEpochMilli()
        } catch (e: DateTimeParseException) {
            null
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Toggle Bouton (toujours en haut à droite)
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
                onValueChange = onStartDateChanged,
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showStartDatePicker = true },
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
                onValueChange = onEndDateChanged,
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

        // BOUTON EFFACER LES FILTRES (en bas et centré)
        Button(
            onClick = onClearFilters,
            enabled = isFilterActive,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Effacer les filtres")
        }
    }

    // --- DIALOGUES CALENDRIER ---

    if (showStartDatePicker) {
        val initialTimestamp = dateToMillis(startDateStr)
        MyDatePickerDialog(
            initialSelectedDateMillis = initialTimestamp,
            onDateSelected = { date ->
                onStartDateChanged(date ?: "")
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        val initialTimestamp = dateToMillis(endDateStr)
        MyDatePickerDialog(
            initialSelectedDateMillis = initialTimestamp,
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
    initialSelectedDateMillis: Long? = null,
    onDateSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    // Initialise l'état du DatePicker avec la valeur passée
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

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
                } else {
                    onDateSelected(null)
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