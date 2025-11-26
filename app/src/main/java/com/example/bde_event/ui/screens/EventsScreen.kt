import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bde_event.MainViewModel

// Dans EventsScreen.kt ou en bas de MainActivity.kt
@Composable
fun EventsScreen() {
    val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            )
            HorizontalDivider() // Note: Divider est déprécié au profit de HorizontalDivider en M3

            // Barre de filtres
            com.example.bde_event.ui.components.FilterBarMobile(
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
                onToggleFilters = { viewModel.filtersVisible = !viewModel.filtersVisible }
            )

            // Liste des événements
            com.example.bde_event.ui.components.WeeklySchedule(
                week = viewModel.filteredEvents,
                modifier = Modifier.weight(1f)
            )
        }
    }
}