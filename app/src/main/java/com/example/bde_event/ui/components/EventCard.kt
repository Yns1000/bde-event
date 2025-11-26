package com.example.bde_event.ui.components // Attention au package

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bde_event.Event

@Composable
fun EventCard(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = "${event.title} — ${event.type}",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "${event.startDate} ${event.time}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!event.location.isNullOrEmpty()) {
                    Text(event.location, style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    text = "Du ${event.startDate} au ${event.endDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}