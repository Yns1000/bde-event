package com.example.bde_event.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.bde_event.R // Important pour accéder à R.drawable

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.academia_icon), // Remplacez par le nom exact de votre fichier
            contentDescription = "Logo Academia",
            modifier = Modifier
                .size(120.dp) // Ajustez la taille selon vos besoins
                .padding(bottom = 16.dp)
        )
        Text(text = "Bienvenue !", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Identifiant") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Peu importe le contenu, on navigue vers la page principale
                // "events_screen" est l'identifiant (route) que nous définirons plus bas
                navController.navigate("events_screen") {
                    // Cette option retire l'écran de login de la "pile" de retour.
                    // Ainsi, si l'utilisateur fait "Retour", il ne revient pas sur le login.
                    popUpTo("login_screen") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Se connecter")
        }
    }
}