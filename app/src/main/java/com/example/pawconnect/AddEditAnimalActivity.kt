
package com.example.pawconnect

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AddEditAnimalActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    private lateinit var ivAnimalImage: ImageView
    private lateinit var etAnimalName: EditText
    private lateinit var spAnimalType: Spinner
    private lateinit var etAnimalDescription: EditText
    private lateinit var btnSaveAnimal: Button

    private var imagePath: String? = null
    private var userId: Int = -1
    private var animalId: Int = -1

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

            if (uri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)

                    if (inputStream != null) {
                        val imageFile = File(
                            filesDir,
                            "animal_${UUID.randomUUID()}.jpg"
                        )

                        val outputStream = FileOutputStream(imageFile)

                        inputStream.copyTo(outputStream)

                        inputStream.close()
                        outputStream.close()

                        imagePath = imageFile.absolutePath

                        ivAnimalImage.setImageURI(Uri.fromFile(imageFile))
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Impossible de charger cette photo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_animal)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)
        animalId = intent.getIntExtra("ANIMAL_ID", -1)

        ivAnimalImage = findViewById(R.id.ivAnimalImage)
        etAnimalName = findViewById(R.id.etAnimalName)
        spAnimalType = findViewById(R.id.spAnimalType)
        etAnimalDescription = findViewById(R.id.etAnimalDescription)

        val btnChooseImage =
            findViewById<Button>(R.id.btnChooseImage)

        btnSaveAnimal =
            findViewById(R.id.btnSaveAnimal)

        val animalTypes = arrayOf(
            "Chat",
            "Chien",
            "Lapin",
            "Oiseau",
            "Autre"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            animalTypes
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spAnimalType.adapter = adapter

        if (animalId != -1) {
            btnSaveAnimal.text = "Enregistrer les modifications"
            loadAnimal()
        }

        btnChooseImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnSaveAnimal.setOnClickListener {
            saveAnimal()
        }
    }

    private fun loadAnimal() {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT name, type, description, image
            FROM animals
            WHERE id = ? AND user_id = ?
            """.trimIndent(),
            arrayOf(
                animalId.toString(),
                userId.toString()
            )
        )

        if (cursor.moveToFirst()) {

            etAnimalName.setText(cursor.getString(0))

            val type = cursor.getString(1)

            val position = (0 until spAnimalType.count).firstOrNull {
                spAnimalType.getItemAtPosition(it).toString() == type
            } ?: 0

            spAnimalType.setSelection(position)

            etAnimalDescription.setText(cursor.getString(2))

            imagePath = cursor.getString(3)

            if (!imagePath.isNullOrEmpty()) {
                val imageFile = File(imagePath!!)

                if (imageFile.exists()) {
                    ivAnimalImage.setImageURI(Uri.fromFile(imageFile))
                }
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

    private fun saveAnimal() {

        val name = etAnimalName.text.toString().trim()
        val type = spAnimalType.selectedItem.toString()
        val description = etAnimalDescription.text.toString().trim()

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(
                this,
                "Veuillez remplir tous les champs",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (imagePath == null) {
            Toast.makeText(
                this,
                "Veuillez choisir une photo",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (userId == -1) {
            Toast.makeText(
                this,
                "Compte de l'association introuvable",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("type", type)
            put("description", description)
            put("image", imagePath)
        }

        val result: Int

        if (animalId == -1) {

            values.put("status", "AVAILABLE")
            values.put("user_id", userId)

            result = db.insert(
                "animals",
                null,
                values
            ).toInt()

        } else {

            result = db.update(
                "animals",
                values,
                "id = ? AND user_id = ?",
                arrayOf(
                    animalId.toString(),
                    userId.toString()
                )
            )
        }

        db.close()

        if (result > 0) {

            val message = if (animalId == -1) {
                "Animal ajouté avec succès !"
            } else {
                "Animal modifié avec succès !"
            }

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                "Erreur lors de l'enregistrement",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
