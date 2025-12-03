package com.example.bde_event

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bde_event.data.ApiClient
import com.example.bde_event.data.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoggedIn by mutableStateOf(false)

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Veuillez remplir tous les champs"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.login(
                    LoginRequest(username, password)
                )
                Log.d("LoginViewModel", "Connexion réussie : ${response.username}")
                isLoggedIn = true
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Erreur de connexion : ${e.message}", e)
                errorMessage = "Identifiants incorrects"
            } finally {
                isLoading = false
            }
        }
    }
}
