package com.example.bde_event.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.bde_event.data.entities.EventEntity
import com.example.bde_event.data.entities.TypeOfEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: EventEntity): Long

    @Insert
    suspend fun insertType(type: TypeOfEventEntity): Long

    @Query("""
      SELECT * FROM event
      WHERE (:minDate IS NULL OR dateMillis >= :minDate)
        AND (:query IS NULL OR name LIKE :query OR description LIKE :query)
      ORDER BY dateMillis DESC
    """)
    fun getEventsFlow(minDate: Long?, query: String?): Flow<List<EventEntity>>

    @Query("SELECT * FROM type_of_event ORDER BY name ASC")
    fun getTypesFlow(): Flow<List<TypeOfEventEntity>>
}
