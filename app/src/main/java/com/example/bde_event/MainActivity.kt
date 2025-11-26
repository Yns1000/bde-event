package com.example.bde_event

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bde_event.ui.screens.EventsScreen
import com.example.bde_event.ui.screens.LoginScreen
import com.example.bde_event.ui.theme.BdeeventTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val systemDark = isDarkSystem(this)

        setContent {
            AppRoot(defaultDark = systemDark)
        }
    }
}

fun isDarkSystem(context: Context): Boolean {
    val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return uiMode == Configuration.UI_MODE_NIGHT_YES
}

@Composable
fun AppRoot(defaultDark: Boolean) {

    var darkTheme by remember { mutableStateOf(defaultDark) }

    BdeeventTheme(darkTheme = darkTheme) {

        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "login_screen"
        ) {

            composable("login_screen") {
                LoginScreen(navController = navController)
            }

            composable("events_screen") {
                EventsScreen(
                    onLogout = {
                        navController.navigate("login_screen") {
                            popUpTo("events_screen") { inclusive = true }
                        }
                    },
                    darkTheme = darkTheme,
                    onThemeToggle = { darkTheme = !darkTheme }
                )
            }
        }
    }
}
