
package com.example.pawconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class AssociationDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_association_dashboard)

        val userId = intent.getIntExtra("USER_ID", -1)

        val cardManageAnimals =
            findViewById<LinearLayout>(R.id.cardManageAnimals)

        val cardManageRequests =
            findViewById<LinearLayout>(R.id.cardManageRequests)

        val cardReports =
            findViewById<LinearLayout>(R.id.cardReports)

        val btnLogout =
            findViewById<Button>(R.id.btnAssociationLogout)

        // Mes animaux
        cardManageAnimals.setOnClickListener {

            val intent = Intent(
                this,
                ManageAnimalsActivity::class.java
            )

            intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }

        // Demandes d'adoption
        cardManageRequests.setOnClickListener {

            val intent = Intent(
                this,
                ManageRequestsActivity::class.java
            )

            intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }

        // Signalements
        cardReports.setOnClickListener {

            val intent = Intent(
                this,
                ReportsActivity::class.java
            )

            intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }

        // Déconnexion
        btnLogout.setOnClickListener {

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}
