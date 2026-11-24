
package com.example.pawconnect

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AnimalDetailsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    private lateinit var ivAnimalDetails: ImageView
    private lateinit var tvAnimalName: TextView
    private lateinit var tvAnimalType: TextView
    private lateinit var tvAnimalDescription: TextView
    private lateinit var tvAnimalStatus: TextView
    private lateinit var btnAddFavorite: Button
    private lateinit var btnAdoptAnimal: Button

    private var animalId = -1
    private var userId = -1
    private var ownerId = -1

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_details)

        dbHelper = DBHelper(this)

        animalId = intent.getIntExtra("ANIMAL_ID", -1)
        userId = intent.getIntExtra("USER_ID", -1)

        ivAnimalDetails = findViewById(R.id.ivAnimalDetails)
        tvAnimalName = findViewById(R.id.tvAnimalName)
        tvAnimalType = findViewById(R.id.tvAnimalType)
        tvAnimalDescription = findViewById(R.id.tvAnimalDescription)
        tvAnimalStatus = findViewById(R.id.tvAnimalStatus)

        btnAddFavorite = findViewById(R.id.btnAddFavorite)
        btnAdoptAnimal = findViewById(R.id.btnAdoptAnimal)

        if (animalId == -1 || userId == -1) {
            Toast.makeText(
                this,
                "Informations manquantes",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadAnimal()
        checkFavorite()

        btnAddFavorite.setOnClickListener {
            toggleFavorite()
        }

        btnAdoptAnimal.setOnClickListener {
            requestAdoption()
        }
    }

    private fun loadAnimal() {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT id, name, type, description, image, status, user_id
            FROM animals
            WHERE id = ?
            """.trimIndent(),
            arrayOf(animalId.toString())
        )

        if (cursor.moveToFirst()) {

            val name = cursor.getString(1)
            val type = cursor.getString(2)
            val description = cursor.getString(3)
            val image = cursor.getString(4)
            val status = cursor.getString(5)

            ownerId = cursor.getInt(6)

            tvAnimalName.text = name
            tvAnimalType.text = type
            tvAnimalDescription.text = description

            tvAnimalStatus.text = when (status) {
                "AVAILABLE" -> "Statut : Disponible à l'adoption"
                "RESERVED" -> "Statut : Réservé"
                "ADOPTED" -> "Statut : Adopté"
                else -> "Statut : $status"
            }

            // Image par défaut
            ivAnimalDetails.setImageResource(
                android.R.drawable.ic_menu_gallery
            )

            // Charger la photo de l'animal
            loadAnimalImage(image)

            btnAdoptAnimal.isEnabled =
                status == "AVAILABLE" && ownerId != userId

            if (ownerId == userId) {
                btnAdoptAnimal.text = "Votre animal"
            } else if (status != "AVAILABLE") {
                btnAdoptAnimal.text = "Indisponible"
            }

        } else {

            Toast.makeText(
                this,
                "Animal introuvable",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        cursor.close()
        db.close()
    }

    private fun loadAnimalImage(image: String?) {

        if (image.isNullOrEmpty()) {
            return
        }

        // Chercher l'image dans Drawable
        val imageResource = resources.getIdentifier(
            image,
            "drawable",
            packageName
        )

        if (imageResource != 0) {

            ivAnimalDetails.setImageResource(imageResource)

        } else {

            // Si ce n'est pas Drawable, vérifier un fichier local
            val imageFile = File(image)

            if (imageFile.exists()) {
                ivAnimalDetails.setImageURI(
                    Uri.fromFile(imageFile)
                )
            }
        }
    }

    private fun checkFavorite() {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT id FROM favorites
            WHERE user_id = ? AND animal_id = ?
            """.trimIndent(),
            arrayOf(userId.toString(), animalId.toString())
        )

        isFavorite = cursor.moveToFirst()

        cursor.close()
        db.close()

        updateFavoriteButton()
    }

    private fun toggleFavorite() {

        val db = dbHelper.writableDatabase

        if (isFavorite) {

            db.delete(
                "favorites",
                "user_id = ? AND animal_id = ?",
                arrayOf(userId.toString(), animalId.toString())
            )

            isFavorite = false

            Toast.makeText(
                this,
                "Animal retiré des favoris",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            val values = android.content.ContentValues()

            values.put("user_id", userId)
            values.put("animal_id", animalId)

            val result = db.insert("favorites", null, values)

            if (result != -1L) {

                isFavorite = true

                Toast.makeText(
                    this,
                    "Animal ajouté aux favoris",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        db.close()

        updateFavoriteButton()
    }


    private fun updateFavoriteButton() {

        if (isFavorite) {
            btnAddFavorite.text = "♥"
            btnAddFavorite.setTextColor(
                android.graphics.Color.parseColor("#C94C61")
            )
        } else {
            btnAddFavorite.text = "♡"
            btnAddFavorite.setTextColor(
                android.graphics.Color.parseColor("#29452D")
            )
        }
    }


    private fun requestAdoption() {

        if (ownerId == userId) {

            Toast.makeText(
                this,
                "Vous ne pouvez pas adopter votre propre animal",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val db = dbHelper.writableDatabase

        val cursor = db.rawQuery(
            """
            SELECT id FROM adoption_requests
            WHERE user_id = ? AND animal_id = ?
            """.trimIndent(),
            arrayOf(userId.toString(), animalId.toString())
        )

        val alreadyRequested = cursor.moveToFirst()

        cursor.close()

        if (alreadyRequested) {

            Toast.makeText(
                this,
                "Vous avez déjà envoyé une demande pour cet animal",
                Toast.LENGTH_SHORT
            ).show()

            db.close()
            return
        }

        val values = android.content.ContentValues()

        values.put("user_id", userId)
        values.put("animal_id", animalId)
        values.put("status", "PENDING")

        val result = db.insert(
            "adoption_requests",
            null,
            values
        )

        db.close()

        if (result != -1L) {

            Toast.makeText(
                this,
                "Votre demande d'adoption a été envoyée",
                Toast.LENGTH_LONG
            ).show()

            btnAdoptAnimal.isEnabled = false
            btnAdoptAnimal.text = "Demande envoyée"

        } else {

            Toast.makeText(
                this,
                "Erreur lors de l'envoi de la demande",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
