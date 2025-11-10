// kotlin
package com.example.bde_event

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.example.bde_event.data.AppDatabase
import com.example.bde_event.data.Repository
import com.example.bde_event.ui.EventListScreen
import com.example.bde_event.ui.MainViewModel
import com.example.bde_event.ui.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room database, Repository, and ViewModel
        val database = AppDatabase.getInstance(this)
        val repository = Repository(database.eventDao())
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContent {
            MaterialTheme {
                EventListScreen(
                    viewModel = viewModel,
                    onSubscribe = {
                        // Open subscribe activity
                    },
                    onAdmin = {
                        // Open admin space
                    }
                )
            }
        }
    }
}
