// kotlin
package com.example.bde_event

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_USERS (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_NAME TEXT NOT NULL" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertUser(name: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
        }
        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    fun getUser(id: Long): User? {
        val db = readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.query(
                TABLE_USERS,
                arrayOf(COL_ID, COL_NAME),
                "$COL_ID = ?",
                arrayOf(id.toString()),
                null, null, null
            )
            return if (cursor.moveToFirst()) {
                val uid = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                User(
                    uid, name,
                    password = ""
                )
            } else null
        } finally {
            cursor?.close()
            db.close()
        }
    }

    fun getAllUsers(): List<User> {
        val db = readableDatabase
        val list = mutableListOf<User>()
        var cursor: Cursor? = null
        try {
            cursor = db.query(TABLE_USERS, arrayOf(COL_ID, COL_NAME), null, null, null, null, "$COL_ID ASC")
            while (cursor.moveToNext()) {
                val uid = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                list.add(User(
                    uid, name,
                    password = ""
                ))
            }
        } finally {
            cursor?.close()
            db.close()
        }
        return list
    }

    fun updateUser(user: User): Int {
        val id = user.id ?: return 0
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, user.username)
        }
        val rows = db.update(TABLE_USERS, values, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun deleteUser(id: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_USERS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    companion object {
        private const val DB_NAME = "users.db"
        private const val DB_VERSION = 1

        private const val TABLE_USERS = "user"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
    }
}