package com.example.bde_event


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bde_event.ui.theme.BdeeventTheme
import com.example.bde_event.ui.screens.LoginScreen // Assurez-vous d'importer votre nouvel écran
import EventsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BdeeventTheme {
                // On crée le contrôleur de navigation
                val navController = rememberNavController()

                // On définit les routes de navigation
                NavHost(navController = navController, startDestination = "login_screen") {

                        // Barre de filtres (connectée au ViewModel)
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
                            onClearFilters = { viewModel.clearFilters() } // CONNEXION DE LA NOUVELLE FONCTION
                        )

                    // Route pour l'écran principal (événements)
                    composable("events_screen") {
                        EventsScreen(
                            onLogout = {
                                // On navigue vers le login
                                navController.navigate("login_screen") {
                                    // On vide la pile de navigation pour qu'un "Retour" ne ramène pas à la page principale
                                    popUpTo("events_screen") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}