package com.example.bde_event.data

import com.example.bde_event.data.model.EventDto
import com.example.bde_event.data.model.TypeOfEventDto
import com.example.bde_event.data.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(
    val username: String,
    val password: String
)

interface ApiService {
    @GET("event")
    suspend fun getEvents(): List<EventDto>

    @POST("event")
    suspend fun createEvent(@Body event: EventDto): EventDto

    @GET("type")
    suspend fun getTypes(): List<TypeOfEventDto>

    @GET("user")
    suspend fun getUsers(): List<UserDto>

    @POST("user/login")
    suspend fun login(@Body credentials: LoginRequest): UserDto
}
