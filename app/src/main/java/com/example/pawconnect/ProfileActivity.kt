
package com.example.pawconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfilePhone: TextView

    private lateinit var btnMyRequests: Button
    private lateinit var btnLogout: Button

    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)

        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        tvProfilePhone = findViewById(R.id.tvProfilePhone)

        btnMyRequests = findViewById(R.id.btnMyRequests)
        btnLogout = findViewById(R.id.btnLogout)

        loadProfile()

        btnMyRequests.setOnClickListener {

            val intent = Intent(this, MyRequestsActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }

    private fun loadProfile() {

        if (userId == -1) {
            Toast.makeText(
                this,
                "Utilisateur introuvable",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT name, email, phone
            FROM users
            WHERE id = ?
            """.trimIndent(),
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {

            tvProfileName.text = cursor.getString(0)
            tvProfileEmail.text = cursor.getString(1)

            val phone = cursor.getString(2)

            tvProfilePhone.text =
                if (phone.isNullOrEmpty()) {
                    "Téléphone non renseigné"
                } else {
                    phone
                }

        } else {

            Toast.makeText(
                this,
                "Profil introuvable",
                Toast.LENGTH_SHORT
            ).show()
        }

        cursor.close()
        db.close()
    }
}
