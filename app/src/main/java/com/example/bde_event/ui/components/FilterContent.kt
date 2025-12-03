package com.example.bde_event.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(
    types: List<String>,
    query: String,
    onQueryChanged: (String) -> Unit,
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    onClearFilters: () -> Unit,
    onCloseDrawer: () -> Unit // Fonction pour fermer la feuille modale
) {
    var typeExpanded by remember { mutableStateOf(false) }

    // Détermination si un filtre est actif (uniquement Recherche ou Type)
    val isFilterActive = remember(query, selectedType) {
        query.isNotBlank() || selectedType != "Tous"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Header du filtre
        Text(
            text = "Filtrer par Catégorie",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start)
        )
        HorizontalDivider()

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

        // 2. Dropdown Type
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded }
        ) {
            OutlinedTextField(
                value = selectedType,
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

        Spacer(modifier = Modifier.height(8.dp))

        // BOUTON EFFACER LES FILTRES (et fermer le drawer)
        Button(
            onClick = {
                onClearFilters()
                onCloseDrawer()
            },
            enabled = isFilterActive,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Effacer et fermer les filtres")
        }
    }
}

// NOTE: MyDatePickerDialog et autres composants liés à la date/heure ont été supprimés/omis
// car ils ne sont plus nécessaires pour FilterContent.