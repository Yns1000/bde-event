// kotlin
package com.example.bde_event

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_USERS (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "password TEXT NOT NULL" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_EMAIL (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT NOT NULL" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_TYPE (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_EVENT (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "idType INTEGER, " +
                    "idUser INTEGER, " +
                    "dateMillis INTEGER NOT NULL, " +
                    "durationMinutes INTEGER NOT NULL, " +
                    "description TEXT, " +
                    "FOREIGN KEY(idType) REFERENCES $TABLE_TYPE($COL_ID), " +
                    "FOREIGN KEY(idUser) REFERENCES $TABLE_USERS($COL_ID)" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TYPE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMAIL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // --- TypeOfEvent ---
    fun insertType(type: TypeOfEvent): Long {
        val db = writableDatabase
        val v = ContentValues().apply {
            put("name", type.name)
            put("description", type.description)
        }
        val id = db.insert(TABLE_TYPE, null, v)
        db.close()
        return id
    }

    fun getAllTypes(): List<TypeOfEvent> {
        val db = readableDatabase
        val list = mutableListOf<TypeOfEvent>()
        var c: Cursor? = null
        try {
            c = db.query(TABLE_TYPE, arrayOf(COL_ID, "name", "description"),
                null, null, null, null, "name ASC")
            while (c.moveToNext()) {
                list.add(
                    TypeOfEvent(
                        c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("description"))
                    )
                )
            }
        } finally {
            c?.close()
            db.close()
        }
        return list
    }

    // --- Event CRUD and query with filters ---
    fun insertEvent(e: Event): Long {
        val db = writableDatabase
        val v = ContentValues().apply {
            put("name", e.name)
            put("idType", e.idType)
            put("idUser", e.idUser)
            put("dateMillis", e.dateMillis)
            put("durationMinutes", e.durationMinutes)
            put("description", e.description)
        }
        val id = db.insert(TABLE_EVENT, null, v)
        db.close()
        return id
    }

    fun updateEvent(e: Event): Int {
        val id = e.id ?: return 0
        val db = writableDatabase
        val v = ContentValues().apply {
            put("name", e.name)
            put("idType", e.idType)
            put("idUser", e.idUser)
            put("dateMillis", e.dateMillis)
            put("durationMinutes", e.durationMinutes)
            put("description", e.description)
        }
        val rows = db.update(TABLE_EVENT, v, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun deleteEvent(id: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_EVENT, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    /**
     * Récupère les événements triés du plus récent au plus ancien.
     * - showPast = false -> exclut les événements dont dateMillis < now
     * - query (nullable) -> filtre title OR description avec LIKE
     */
    fun getEvents(showPast: Boolean, query: String?): List<Event> {
        val db = readableDatabase
        val list = mutableListOf<Event>()
        var c: Cursor? = null
        try {
            val where = mutableListOf<String>()
            val args = mutableListOf<String>()

            if (!showPast) {
                where.add("dateMillis >= ?")
                args.add(System.currentTimeMillis().toString())
            }
            if (!query.isNullOrBlank()) {
                where.add("(name LIKE ? OR description LIKE ?)")
                val q = "%${query.trim()}%"
                args.add(q); args.add(q)
            }
            val selection = if (where.isEmpty()) null else where.joinToString(" AND ")
            c = db.query(
                TABLE_EVENT,
                arrayOf(COL_ID, "name", "idType", "idUser", "dateMillis", "durationMinutes", "description"),
                selection,
                if (args.isEmpty()) null else args.toTypedArray(),
                null, null,
                "dateMillis DESC"
            )
            while (c.moveToNext()) {
                list.add(
                    Event(
                        c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getLongOrNull("idType"),
                        c.getLongOrNull("idUser"),
                        c.getLong(c.getColumnIndexOrThrow("dateMillis")),
                        c.getInt(c.getColumnIndexOrThrow("durationMinutes")),
                        c.getString(c.getColumnIndexOrThrow("description"))
                    )
                )
            }
        } finally {
            c?.close()
            db.close()
        }
        return list
    }

    // helper to read nullable long column
    private fun Cursor.getLongOrNull(columnName: String): Long? {
        val idx = getColumnIndex(columnName)
        if (idx < 0 || isNull(idx)) return null
        return getLong(idx)
    }

    companion object {
        private const val DB_NAME = "app.db"
        private const val DB_VERSION = 1

        private const val TABLE_USERS = "Users"
        private const val TABLE_EMAIL = "Email"
        private const val TABLE_TYPE = "TypeOfEvent"
        private const val TABLE_EVENT = "Event"

        private const val COL_ID = "id"
    }
}