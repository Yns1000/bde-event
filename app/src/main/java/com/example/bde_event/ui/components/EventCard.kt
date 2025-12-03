package com.example.bde_event.ui.components

import androidx.compose.foundation.BorderStroke // Import nécessaire pour BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bde_event.Event // Assurez-vous que votre classe Event est bien accessible
import androidx.compose.foundation.border

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(event: Event) {
    // État pour contrôler l'affichage du Bottom Sheet (Détails)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var isSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { isSheetOpen = true } // Ouvre la feuille au clic sur la carte
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp) // Utilisez la même forme que la carte
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        // AJOUT DE LA BORDURE
    ) {
        Column(
            modifier = Modifier.padding(20.dp, 10.dp)
        ) {
            // --- LIGNE 1 : Titre et Bouton Détails (Flèche) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Titre de l'événement (L'élément principal)
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)

                )

                // Icône d'action pour ouvrir le tiroir
                IconButton(onClick = { isSheetOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Ouvrir les détails de l'événement",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- LIGNE 2 : Date (Seule information secondaire conservée) ---
            EventInfoRow(icon = Icons.Default.CalendarToday, text = "Le ${event.startDate}")

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // --- Composant Modal Bottom Sheet ---
    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                isSheetOpen = false
            },
            sheetState = sheetState
        ) {
            EventDetailSheet(
                event = event,
                onEditClick = {
                    // Logique de modification ici, puis fermeture de la feuille
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            isSheetOpen = false
                        }
                    }
                }
            )
        }
    }
}

// ----------------------------------------------------------------------
// Contenu du Bottom Sheet (Détails de l'événement)
// ----------------------------------------------------------------------

@Composable
fun EventDetailSheet(
    event: Event,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // --- Header : Titre et Type ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryBadge(type = event.type)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Détails de l'événement ---

        // Date & Heure
        EventInfoRow(icon = Icons.Default.Event, text = "Du ${event.startDate} au ${event.endDate} à ${event.time}")
        Spacer(modifier = Modifier.height(12.dp))

        // Lieu
        EventInfoRow(icon = Icons.Default.LocationOn, text = event.location ?: "Lieu à définir")
        Spacer(modifier = Modifier.height(24.dp))

        Divider()
        Spacer(modifier = Modifier.height(24.dp))

        // Description
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            // Correction de l'erreur String?
            text = event.description ?: "Pas de description disponible.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton d'Action
        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Modifier l'événement")
        }
        Spacer(modifier = Modifier.height(32.dp)) // Padding pour le bas de l'écran
    }
}

// ----------------------------------------------------------------------
// Fonctions utilitaires
// ----------------------------------------------------------------------

@Composable
fun EventInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CategoryBadge(type: String) {
    val (bgColor, textColor) = when (type.lowercase()) {
        "sport" -> Pair(Color(0xFFFFE0B2).copy(alpha = 0.7f), Color(0xFFE65100))
        "réunion", "reunion" -> Pair(Color(0xFFBBDEFB).copy(alpha = 0.7f), Color(0xFF0D47A1))
        "culture", "soirée" -> Pair(Color(0xFFE1BEE7).copy(alpha = 0.7f), Color(0xFF4A148C))
        "atelier" -> Pair(Color(0xFFC8E6C9).copy(alpha = 0.7f), Color(0xFF1B5E20))
        else -> Pair(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f), MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Text(
            text = type.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = textColor
        )
    }
}