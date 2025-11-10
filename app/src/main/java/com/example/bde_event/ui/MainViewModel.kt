package com.example.bde_event.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bde_event.data.Repository
import com.example.bde_event.data.entities.EventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val repo: Repository) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _showPast = MutableStateFlow(false)

    // Simple auth state (simulated)
    private val _isAuthor = MutableStateFlow(false)
    val isAuthor: StateFlow<Boolean> = _isAuthor.asStateFlow()

    private val eventsFlow = combine(_query, _showPast) { q, sp ->
        Pair(q, sp)
    }.flatMapLatest { (q, sp) ->
        repo.getEventsFlow(showPast = sp, query = q)
    }

    val events: StateFlow<List<EventEntity>> = eventsFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun setQuery(q: String) {
        _query.value = q
    }

    fun setShowPast(show: Boolean) {
        _showPast.value = show
    }

    fun loginAsAuthor() {
        _isAuthor.value = true
    }

    fun logout() {
        _isAuthor.value = false
    }
}
