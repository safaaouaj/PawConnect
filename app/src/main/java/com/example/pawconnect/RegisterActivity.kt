
package com.example.pawconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DBHelper(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val btnCreateAccount =
            findViewById<Button>(R.id.btnCreateAccount)

        val tvGoLogin =
            findViewById<TextView>(R.id.tvGoLogin)

        btnCreateAccount.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Veuillez remplir tous les champs",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email).matches()) {

                Toast.makeText(
                    this,
                    "Adresse e-mail invalide",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (password.length < 6) {

                Toast.makeText(
                    this,
                    "Le mot de passe doit contenir au moins 6 caractères",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val result = dbHelper.registerUser(
                    name,
                    email,
                    password,
                    phone,
                    "USER"
                )

                if (result != -1L) {

                    Toast.makeText(
                        this,
                        "Inscription réussie ! Connectez-vous.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Cet e-mail existe déjà",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        tvGoLogin.setOnClickListener {
            finish()
        }
    }
}
