import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bde_event.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp // Ou Icons.Filled.ExitToApp selon la version
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bde_event.ui.components.FilterBarMobile
import com.example.bde_event.ui.components.WeeklySchedule

// Si automirrored n'est pas trouvé: import androidx.compose.material.icons.filled.ExitToApp

// Dans EventsScreen.kt ou en bas de MainActivity.kt
@Composable
fun EventsScreen(onLogout: () -> Unit) {
    val viewModel: MainViewModel = viewModel()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. On remplace le titre simple par une Row contenant Titre + Bouton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Espacement max entre les éléments
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Événements",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                IconButton(onClick = { onLogout() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Se déconnecter",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider()

            // Barre de filtres
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

            // Liste des événements
            WeeklySchedule(
                week = viewModel.filteredEvents,
                modifier = Modifier.weight(1f)
            )
        }
    }
}