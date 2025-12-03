package com.example.bde_event.data.source

import android.util.Log
import com.example.bde_event.data.ApiClient
import com.example.bde_event.api.services.EventApi
import com.example.bde_event.api.models.Event as GeneratedEvent

class RemoteDataSourceImpl(private val eventApi: EventApi = ApiClient.eventApi) : RemoteDataSource {

    override suspend fun getEvents(): List<GeneratedEvent> {
        return try {
            eventApi.getEvents().body() ?: emptyList()
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Erreur getEvents: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun addEvent(event: GeneratedEvent): GeneratedEvent {
        return try {
            val response = eventApi.createEvent(event)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("RemoteDataSource", "HTTP ${response.code()}: $errorBody")
                throw Exception("Erreur HTTP ${response.code()}: $errorBody")
            }

            response.body() ?: throw Exception("Le serveur n'a pas retourné de données")
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Échec addEvent: ${e.message}", e)
            throw e
        }
    }
}
