
package com.example.pawconnect

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageRequestsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var rvAdoptionRequests: RecyclerView
    private lateinit var adapter: AdoptionRequestAdapter

    private val requests = ArrayList<AdoptionRequestItem>()

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_requests)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)

        rvAdoptionRequests =
            findViewById(R.id.rvAdoptionRequests)

        rvAdoptionRequests.layoutManager =
            LinearLayoutManager(this)

        adapter = AdoptionRequestAdapter(
            requests,
            { request ->
                confirmAccept(request)
            },
            { request ->
                confirmReject(request)
            }
        )

        rvAdoptionRequests.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadRequests()
    }

    // Charger les demandes concernant les animaux de l'association
    private fun loadRequests() {

        requests.clear()

        if (userId == -1) {
            Toast.makeText(
                this,
                "Compte de l'association introuvable",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT 
                adoption_requests.id,
                animals.name,
                users.name,
                adoption_requests.status
            FROM adoption_requests
            INNER JOIN animals
                ON adoption_requests.animal_id = animals.id
            INNER JOIN users
                ON adoption_requests.user_id = users.id
            WHERE animals.user_id = ?
            ORDER BY adoption_requests.id DESC
            """.trimIndent(),
            arrayOf(userId.toString())
        )

        cursor.use {

            while (it.moveToNext()) {

                val request = AdoptionRequestItem(
                    id = it.getInt(0),
                    animalName = it.getString(1),
                    userName = it.getString(2),
                    status = it.getString(3)
                )

                requests.add(request)
            }
        }

        db.close()

        adapter.notifyDataSetChanged()

        if (requests.isEmpty()) {
            Toast.makeText(
                this,
                "Aucune demande d'adoption pour le moment",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Confirmer l'acceptation
    private fun confirmAccept(request: AdoptionRequestItem) {

        AlertDialog.Builder(this)
            .setTitle("Accepter la demande")
            .setMessage(
                "Voulez-vous accepter la demande pour ${request.animalName} ?"
            )
            .setPositiveButton("Accepter") { _, _ ->
                updateRequest(request, "ACCEPTED")
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    // Confirmer le refus
    private fun confirmReject(request: AdoptionRequestItem) {

        AlertDialog.Builder(this)
            .setTitle("Refuser la demande")
            .setMessage(
                "Voulez-vous refuser cette demande d'adoption ?"
            )
            .setPositiveButton("Refuser") { _, _ ->
                updateRequest(request, "REJECTED")
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    // Enregistrer la décision dans SQLite
    private fun updateRequest(
        request: AdoptionRequestItem,
        newStatus: String
    ) {

        val db = dbHelper.writableDatabase

        val values = android.content.ContentValues()
        values.put("status", newStatus)

        val result = db.update(
            "adoption_requests",
            values,
            """
            id = ? AND animal_id IN (
                SELECT id FROM animals WHERE user_id = ?
            )
            """.trimIndent(),
            arrayOf(
                request.id.toString(),
                userId.toString()
            )
        )

        if (result > 0 && newStatus == "ACCEPTED") {

            val animalValues = android.content.ContentValues()
            animalValues.put("status", "RESERVED")

            db.update(
                "animals",
                animalValues,
                """
                id = (
                    SELECT animal_id
                    FROM adoption_requests
                    WHERE id = ?
                ) AND user_id = ?
                """.trimIndent(),
                arrayOf(
                    request.id.toString(),
                    userId.toString()
                )
            )
        }

        db.close()

        if (result > 0) {

            val message = if (newStatus == "ACCEPTED") {
                "Demande acceptée avec succès"
            } else {
                "Demande refusée"
            }

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()

            loadRequests()

        } else {

            Toast.makeText(
                this,
                "Erreur lors de la mise à jour",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
