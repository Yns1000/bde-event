// kotlin
package com.example.bde_event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventItemComposable(event: Event, onClick: (Event) -> Unit) {
    val df = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    Column(modifier = Modifier
        .clickable { onClick(event) }
        .padding(12.dp)
    ) {
        Text(text = event.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Text(text = df.format(Date(event.dateMillis)), style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
        if (!event.description.isNullOrBlank()) {
            Text(text = event.description, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }
    }
}