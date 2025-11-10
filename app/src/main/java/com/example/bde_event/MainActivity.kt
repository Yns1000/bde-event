// kotlin
package com.example.bde_event

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabaseHelper
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabaseHelper(this)
        prefs = getSharedPreferences("app", MODE_PRIVATE)

        setContent {
            MaterialTheme {
                val isAuthorPref = remember { mutableStateOf(prefs.getBoolean("isAuthor", false)) }
                EventListScreen(
                    db = db,
                    isAuthor = isAuthorPref.value,
                    onLogin = {
                        prefs.edit { putBoolean("isAuthor", true) }
                        isAuthorPref.value = true
                    },
                    onLogout = {
                        prefs.edit { putBoolean("isAuthor", false) }
                        isAuthorPref.value = false
                    },
                    onSubscribe = {
                        //startActivity(Intent(this@MainActivity, SubscribeActivity::class.java))
                    },
                    onAdmin = {
                        // ouvrir l'espace admin si implémenté
                    }
                )
            }
        }
    }
}

@Composable
fun EventListScreen(
    db: AppDatabaseHelper,
    isAuthor: Boolean,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onSubscribe: () -> Unit,
    onAdmin: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var showPast by rememberSaveable { mutableStateOf(false) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(query, showPast) {
        loading = true
        events = withContext(Dispatchers.IO) {
            db.getEvents(showPast = showPast, query = if (query.isBlank()) null else query)
        }
        loading = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onSubscribe) { Text(text = "S'abonner à la newsletter") }

            Row {
                if (!isAuthor) {
                    Button(onClick = onLogin, modifier = Modifier.padding(start = 8.dp)) { Text("Connexion") }
                } else {
                    Button(onClick = onLogout, modifier = Modifier.padding(start = 8.dp)) { Text("Déconnexion") }
                    Button(onClick = onAdmin, modifier = Modifier.padding(start = 8.dp)) { Text("Espace administrateur") }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Rechercher titre / description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Start) {
            Text(text = "Afficher les événements passés", modifier = Modifier.alignByBaseline())
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = showPast, onCheckedChange = { showPast = it })
        }

        if (loading) {
            Text("Chargement...", modifier = Modifier.padding(16.dp))
        } else {
            androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(events.size) { idx ->
                    val e = events[idx]
                    EventItemComposable(event = e, onClick = { /* ouvrir détail */ })
                    Divider()
                }
            }
        }
    }
}