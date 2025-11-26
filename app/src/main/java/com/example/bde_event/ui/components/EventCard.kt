package com.example.bde_event.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
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

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun EventCard(event: Event) {
    // État pour contrôler l'ouverture de la popup
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // --- LIGNE 1 : Catégorie (Badge), Titre, et Bouton Menu ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Partie Gauche : Badge et Titre
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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

                // Bouton Menu (Trois points verticaux)
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Ouvrir le menu de l'événement",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // --- LIGNE 2 : Infos (Date, Heure, Lieu) avec Icônes ---

            // Date
            EventInfoRow(
                icon = Icons.Default.CalendarToday,
                text = "Le ${event.startDate}"
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

    // --- Composant AlertDialog (Popup) ---
    if (showDialog) {
        EventDetailDialog(
            onDismissRequest = { showDialog = false },
            onEditClick = {
                /* Logique de modification ici */
                showDialog = false
            }
        )
    }
}

// Composant de la Popup (AlertDialog) - TAILLE AGGRANDIE ET SKELETON
@Composable
fun EventDetailDialog(
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,

        // MODIFICATEUR POUR AGRANDIR LA POPUP
        modifier = Modifier.fillMaxHeight(0.7f).fillMaxWidth(1f), // Prend 70% de la hauteur et 90% de la largeur

        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Titre de la popup
                Text(
                    "Détails de l'événement",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                // Croix pour fermer
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer la fenêtre de détails",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },

        // Contenu de la popup : UTILISATION DU SKELETON
        text = {
            Column(modifier = Modifier.fillMaxHeight()) { // Permet au skeleton de prendre l'espace
                EventDetailSkeleton()
            }
        },

        // Actions (bouton "Modifier")
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center // Vous pouvez centrer le bouton si vous le souhaitez, mais fillMaxWidth le rendra plein
            ) {
                Button(
                    onClick = onEditClick,
                    // 2. Appliquez le modificateur fillMaxWidth() directement au bouton
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Modifier")
                }
            }
        }
    )
}

// Composant pour simuler le contenu détaillé (SKELETON)
@Composable
fun EventDetailSkeleton() {
    val simulatedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) // Couleur grise claire
    val radius = 4.dp

    Column(modifier = Modifier.fillMaxSize()) {

        // Ligne 1 (Titre/Date)
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.9f) // 90% de la largeur
                .height(20.dp)
                .background(simulatedColor, RoundedCornerShape(radius))
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ligne 2 (Info courte/Lieu)
        Spacer(
            modifier = Modifier
                .fillMaxWidth(0.6f) // 60% de la largeur
                .height(16.dp)
                .background(simulatedColor, RoundedCornerShape(radius))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Simulation du texte détaillé
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Prend l'espace restant pour simuler un grand bloc
                .background(simulatedColor, RoundedCornerShape(radius))
        )
    }
}

// Petit composant pour afficher une ligne d'info avec icône (Inchangé)
@Composable
fun EventInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
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

// Composant pour le Badge de catégorie (Inchangé)
@Composable
fun CategoryBadge(type: String) {
    // On choisit une couleur en fonction du type
    val (bgColor, textColor) = when (type.lowercase()) {
        "sport" -> Pair(Color(0xFFFFE0B2), Color(0xFFE65100))
        "réunion", "reunion" -> Pair(Color(0xFFBBDEFB), Color(0xFF0D47A1))
        "culture", "soirée" -> Pair(Color(0xFFE1BEE7), Color(0xFF4A148C))
        "atelier" -> Pair(Color(0xFFC8E6C9), Color(0xFF1B5E20))
        else -> Pair(Color(0xFFF5F5F5), Color(0xFF616161))
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