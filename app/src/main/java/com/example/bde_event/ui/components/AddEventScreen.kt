package com.example.bde_event.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

    // Validation
    val isFormValid = title.isNotBlank() && dateStr.isNotBlank() && startTimeStr.isNotBlank() && endTimeStr.isNotBlank()

    Scaffold(
        topBar = {
            // Utilisation de CenterAlignedTopAppBar pour un style M3 plus moderne
            CenterAlignedTopAppBar(
                title = { Text("Nouvel événement", fontWeight = FontWeight.SemiBold) },
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
                .padding(horizontal = 16.dp) // Padding horizontal plus constant
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Permet de faire défiler si le contenu est trop long
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Espace après la barre supérieure

            // --- 1. Détails de base ---
            // Titre
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titre de l'événement *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Lieu
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lieu (Salle, Gymnase...)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Type
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type d'événement") },
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
                    types.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = { type = t; typeExpanded = false }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // --- 2. Date et Heure ---
            Text("Quand l'événement aura-t-il lieu ?", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))

            // Date
            OutlinedTextField(
                value = dateStr,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date de l'événement *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Choisir date",
                            tint = MaterialTheme.colorScheme.primary // Couleur primaire pour l'accentuation
                        )
                    }
                }
            )

            // Heures
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Heure de début
                OutlinedTextField(
                    value = startTimeStr,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Début *") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartTimePicker = true },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showStartTimePicker = true }) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = "Heure début",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                // Heure de fin
                OutlinedTextField(
                    value = endTimeStr,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fin *") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndTimePicker = true },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showEndTimePicker = true }) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = "Heure fin",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // --- 3. Description ---
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description de l'événement") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 6,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. Boutons d'Action (En bas) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bouton Annuler (moins de poids visuel)
                FilledTonalButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Annuler")
                }

                // Bouton Valider (action principale)
                Button(
                    onClick = {
                        if (isFormValid) {
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
                                // Gérer erreur de parsing de date ici
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isFormValid
                ) {
                    Text("Ajouter")
                }
            }
        }
    }

    // --- DIALOGUES ---
    if (showDatePicker) {
        // NOTE: MyDatePickerDialog doit être défini ailleurs et utiliser le format YYYY-MM-DD
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