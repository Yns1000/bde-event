package com.example.bde_event.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bde_event.Event
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    onSave: (Event) -> Unit,
    onCancel: () -> Unit
) {
    // États du formulaire
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Réunion") }

    var description by remember { mutableStateOf("") }

    // Dates et Heures
    var dateStr by remember { mutableStateOf("") }
    var startTimeStr by remember { mutableStateOf("") }
    var endTimeStr by remember { mutableStateOf("") }

    // États d'affichage des dialogues
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var typeExpanded by remember { mutableStateOf(false) }
    val types = listOf("Sport", "Réunion", "Culture", "Forum", "Atelier", "Autre")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvel événement") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Titre
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Titre de l'événement") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // 2. Lieu
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Lieu (Salle, Gymnase...)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // 3. Type
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Type d'événement") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    types.forEach { t ->
                        DropdownMenuItem(text = { Text(t) }, onClick = { type = t; typeExpanded = false })
                    }
                }
            }

            // 4. Date
            OutlinedTextField(
                value = dateStr,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Choisir date")
                    }
                }
            )

            // 5. Heures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startTimeStr,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Début") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartTimePicker = true },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showStartTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Heure début")
                        }
                    }
                )

                OutlinedTextField(
                    value = endTimeStr,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Fin") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndTimePicker = true },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showEndTimePicker = true }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Heure fin")
                        }
                    }
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Description (optionnel)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3, // On permet plusieurs lignes
                maxLines = 5,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bouton Valider
            Button(
                onClick = {
                    if (title.isNotBlank() && dateStr.isNotBlank() && startTimeStr.isNotBlank() && endTimeStr.isNotBlank()) {
                        try {
                            val date = LocalDate.parse(dateStr)
                            val formattedTime = "$startTimeStr - $endTimeStr"

                            val newEvent = Event(
                                id = (0..10000).random(),
                                title = title,
                                startDate = date,
                                endDate = date,
                                time = formattedTime,
                                location = location,
                                type = type,
                                description = description
                            )
                            onSave(newEvent)
                        } catch (e: Exception) {
                            // Gérer erreur
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && dateStr.isNotBlank() && startTimeStr.isNotBlank() && endTimeStr.isNotBlank()
            ) {
                Text("Ajouter l'événement")
            }
        }
    }

    // --- DIALOGUES ---
    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { d -> dateStr = d ?: ""; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showStartTimePicker) {
        MyTimePickerDialog(
            title = "Heure de début",
            onTimeSelected = { time -> startTimeStr = time; showStartTimePicker = false },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        MyTimePickerDialog(
            title = "Heure de fin",
            onTimeSelected = { time -> endTimeStr = time; showEndTimePicker = false },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

// ... Ton composant MyTimePickerDialog reste en bas, inchangé ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTimePickerDialog(
    title: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(is24Hour = true)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Formater l'heure en HH:mm (ex: 09:05)
                val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                onTimeSelected(localTime.format(formatter))
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
        title = { Text(title) },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}