
package com.example.pawconnect

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "pawconnect.db"
        private const val DATABASE_VERSION = 1
    }

    // Activation des clés étrangères
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {

        // TABLE USERS
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('USER', 'ASSOCIATION')),
                phone TEXT
            )
        """)

        // TABLE ANIMALS
        db.execSQL("""
            CREATE TABLE animals (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                description TEXT,
                image TEXT,
                status TEXT DEFAULT 'AVAILABLE'
                    CHECK(status IN ('AVAILABLE', 'RESERVED', 'ADOPTED')),
                user_id INTEGER NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)

        // TABLE ADOPTION_REQUESTS
        db.execSQL("""
            CREATE TABLE adoption_requests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                animal_id INTEGER NOT NULL,
                status TEXT DEFAULT 'PENDING'
                    CHECK(status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_id) REFERENCES animals(id) ON DELETE CASCADE
            )
        """)

        // TABLE FAVORITES
        db.execSQL("""
            CREATE TABLE favorites (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                animal_id INTEGER NOT NULL,
                UNIQUE(user_id, animal_id),
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(animal_id) REFERENCES animals(id) ON DELETE CASCADE
            )
        """)

        // TABLE REPORTS
        db.execSQL("""
            CREATE TABLE reports (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description TEXT NOT NULL,
                location TEXT NOT NULL,
                image TEXT,
                user_id INTEGER NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Les futures modifications seront ajoutées ici.
    }

    // INSCRIPTION D'UN UTILISATEUR OU D'UNE ASSOCIATION
    fun registerUser(
        name: String,
        email: String,
        password: String,
        phone: String,
        role: String
    ): Long {

        val db = writableDatabase

        val values = ContentValues()
        values.put("name", name)
        values.put("email", email)
        values.put("password", password)
        values.put("phone", phone)
        values.put("role", role)

        return db.insert("users", null, values)
    }

    // CONNEXION
    fun loginUser(email: String, password: String): User? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT id, name, email, phone, role FROM users WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )

        return cursor.use {

            if (it.moveToFirst()) {

                User(
                    id = it.getInt(0),
                    name = it.getString(1),
                    email = it.getString(2),
                    phone = if (it.isNull(3)) null else it.getString(3),
                    role = it.getString(4)
                )

            } else {
                null
            }
        }
    }
}
