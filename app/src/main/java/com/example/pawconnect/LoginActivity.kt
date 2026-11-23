
package com.example.pawconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)

        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)
        val tvAssociationRegister =
            findViewById<TextView>(R.id.tvAssociationRegister)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Veuillez remplir tous les champs",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val user = dbHelper.loginUser(email, password)

                if (user != null) {

                    val intent = if (user.role == "USER") {
                        Intent(this, MainActivity::class.java)
                    } else {
                        Intent(this, AssociationDashboardActivity::class.java)
                    }

                    intent.putExtra("USER_ID", user.id)
                    intent.putExtra("USER_NAME", user.name)

                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "E-mail ou mot de passe incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        tvGoRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        tvAssociationRegister.setOnClickListener {
            startActivity(
                Intent(this, AssociationRegisterActivity::class.java)
            )
        }
    }
}
