// kotlin
package com.example.bde_event.data

import com.example.bde_event.api.services.EventApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

object ApiClient {

    // Adaptateur personnalisé pour LocalDate (inchangé)
    private class LocalDateAdapter : JsonAdapter<LocalDate>() {
        private val isoFormatter = DateTimeFormatter.ISO_DATE

        override fun fromJson(reader: JsonReader): LocalDate? {
            if (reader.peek() == JsonReader.Token.NULL) {
                reader.nextNull<Unit>()
                return null
            }

            val str = reader.nextString() ?: return null
            if (str.isBlank()) return null

            try { return LocalDate.parse(str, isoFormatter) } catch (_: DateTimeParseException) { }
            try { return OffsetDateTime.parse(str).toLocalDate() } catch (_: DateTimeParseException) { }
            try { return LocalDateTime.parse(str).toLocalDate() } catch (_: DateTimeParseException) { }
            return try { if (str.length >= 10) LocalDate.parse(str.substring(0, 10), isoFormatter) else null } catch (_: Exception) { null }
        }

        override fun toJson(writer: JsonWriter, value: LocalDate?) {
            if (value == null) writer.nullValue() else writer.value(value.format(isoFormatter))
        }
    }

    private val moshi = Moshi.Builder()
        .add(LocalDate::class.java, LocalDateAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3001/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // service généré par OpenAPI (si nécessaire)
    val eventApi: EventApi = retrofit.create(EventApi::class.java)

    // service manuellement défini dans le projet — expose getTypes()
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
