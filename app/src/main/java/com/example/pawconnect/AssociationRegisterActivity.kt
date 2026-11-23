
package com.example.pawconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AssociationRegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_association_register)

        dbHelper = DBHelper(this)

        val etName = findViewById<EditText>(R.id.etAssociationName)
        val etEmail = findViewById<EditText>(R.id.etAssociationEmail)
        val etPhone = findViewById<EditText>(R.id.etAssociationPhone)
        val etPassword = findViewById<EditText>(R.id.etAssociationPassword)

        val btnRegister =
            findViewById<Button>(R.id.btnAssociationRegister)

        btnRegister.setOnClickListener {

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
                    "ASSOCIATION"
                )

                if (result != -1L) {

                    Toast.makeText(
                        this,
                        "Association inscrite avec succès !",
                        Toast.LENGTH_SHORT
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
    }
}
