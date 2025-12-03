package com.example.bde_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bde_event.ui.screens.EventsScreen
import com.example.bde_event.ui.theme.BdeeventTheme
import com.example.bde_event.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BdeeventTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login_screen") {

                    composable("login_screen") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("events_screen") {
                                    popUpTo("login_screen") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("events_screen") {
                        EventsScreen(
                            onLogout = {
                                navController.navigate("login_screen") {
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
