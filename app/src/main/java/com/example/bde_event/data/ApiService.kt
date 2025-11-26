package com.example.bde_event.data

import com.example.bde_event.data.model.* // Importe tous tes DTOs
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    // --- ÉVÉNEMENTS ---
    @GET("events")
    suspend fun getEvents(): List<EventDto>

    @POST("events")
    suspend fun createEvent(@Body event: EventDto): EventDto

    // --- TYPES (Pour remplir le filtre déroulant dynamiquement !) ---
    @GET("types")
    suspend fun getTypes(): List<TypeOfEventDto>

    // --- USERS (Pour le login ou le profil) ---
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}