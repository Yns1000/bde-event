package com.example.bde_event.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bde_event.data.dao.EventDao
import com.example.bde_event.data.entities.EmailEntity
import com.example.bde_event.data.entities.EventEntity
import com.example.bde_event.data.entities.TypeOfEventEntity
import com.example.bde_event.data.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [EventEntity::class, TypeOfEventEntity::class, UserEntity::class, EmailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate on IO thread
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulate(getInstance(context).eventDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulate(dao: EventDao) {
            // Create event types
            val type1 = TypeOfEventEntity(name = "Concert", description = "Music gigs")
            val type2 = TypeOfEventEntity(name = "Conférence", description = "Talks and presentations")
            val type3 = TypeOfEventEntity(name = "Sport", description = "Athletic events")
            val t1Id = dao.insertType(type1)
            val t2Id = dao.insertType(type2)
            val t3Id = dao.insertType(type3)

            // Create sample events
            dao.insert(
                EventEntity(
                    name = "Concert: The Composers",
                    idType = t1Id,
                    idUser = null,
                    dateMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000, // tomorrow
                    durationMinutes = 120,
                    description = "An evening of contemporary compositions."
                )
            )
            dao.insert(
                EventEntity(
                    name = "Conférence: Compose for Mobile",
                    idType = t2Id,
                    idUser = null,
                    dateMillis = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000, // next week
                    durationMinutes = 90,
                    description = "Learn about Jetpack Compose and architecture."
                )
            )
            dao.insert(
                EventEntity(
                    name = "Retro Party",
                    idType = t1Id,
                    idUser = null,
                    dateMillis = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000, // 3 days ago (past)
                    durationMinutes = 240,
                    description = "A past event to test filtering."
                )
            )
            dao.insert(
                EventEntity(
                    name = "Basketball Tournament",
                    idType = t3Id,
                    idUser = null,
                    dateMillis = System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000, // in 3 days
                    durationMinutes = 180,
                    description = "Inter-university basketball competition."
                )
            )
            dao.insert(
                EventEntity(
                    name = "Tech Talk: AI and Machine Learning",
                    idType = t2Id,
                    idUser = null,
                    dateMillis = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000, // in 2 weeks
                    durationMinutes = 60,
                    description = "Exploring the future of AI in everyday applications."
                )
            )
            dao.insert(
                EventEntity(
                    name = "Old Concert",
                    idType = t1Id,
                    idUser = null,
                    dateMillis = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000, // 10 days ago (past)
                    durationMinutes = 150,
                    description = "Another past event for testing."
                )
            )
        }
    }
}
