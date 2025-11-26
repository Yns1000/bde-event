package com.example.bde_event.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bde_event.Event

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Un peu d'espace entre les cartes
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Ombre portée
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Fond blanc/sombre standard
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // --- LIGNE 1 : Catégorie (Badge) et Titre ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Badge de catégorie coloré
                CategoryBadge(type = event.type)

                Spacer(modifier = Modifier.width(12.dp))

                // Titre de l'événement
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // --- LIGNE 2 : Infos (Date, Heure, Lieu) avec Icônes ---

            // Date
            EventInfoRow(
                icon = Icons.Default.CalendarToday,
                text = "Le ${event.startDate}" // Tu pourras formater la date ici plus tard
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Heure
            EventInfoRow(
                icon = Icons.Default.AccessTime,
                text = event.time
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Lieu (si dispo)
            if (!event.location.isNullOrEmpty()) {
                EventInfoRow(
                    icon = Icons.Default.LocationOn,
                    text = event.location
                )
            }
        }
    }
}

// Petit composant pour afficher une ligne d'info avec icône
@Composable
fun EventInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary, // Couleur de l'app
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Composant pour le Badge de catégorie (Sport, Réunion, etc.)
@Composable
fun CategoryBadge(type: String) {
    // On choisit une couleur en fonction du type
    val (bgColor, textColor) = when (type.lowercase()) {
        "sport" -> Pair(Color(0xFFFFE0B2), Color(0xFFE65100)) // Orange
        "réunion", "reunion" -> Pair(Color(0xFFBBDEFB), Color(0xFF0D47A1)) // Bleu
        "culture", "soirée" -> Pair(Color(0xFFE1BEE7), Color(0xFF4A148C)) // Violet
        "atelier" -> Pair(Color(0xFFC8E6C9), Color(0xFF1B5E20)) // Vert
        else -> Pair(Color(0xFFF5F5F5), Color(0xFF616161)) // Gris par défaut
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = type.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}