package com.example.bde_event


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bde_event.ui.screens.EventsScreen
import com.example.bde_event.ui.theme.BdeeventTheme
import com.example.bde_event.ui.screens.LoginScreen // Assurez-vous d'importer votre nouvel écran
// Importez EventsScreen s'il est dans un autre fichier

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

                    // Route pour l'écran de connexion
                    composable("login_screen") {
                        LoginScreen(navController = navController)
                    }

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