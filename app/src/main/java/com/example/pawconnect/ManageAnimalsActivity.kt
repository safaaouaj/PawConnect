
package com.example.pawconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageAnimalsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var rvAnimals: RecyclerView
    private lateinit var adapter: AssociationAnimalAdapter

    private val animals = ArrayList<Animal>()

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_animals)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)

        rvAnimals = findViewById(R.id.rvAnimals)

        val btnAddAnimal =
            findViewById<Button>(R.id.btnAddAnimal)

        rvAnimals.layoutManager = LinearLayoutManager(this)

        adapter = AssociationAnimalAdapter(
            animals,
            { animal ->
                val intent = Intent(
                    this,
                    AddEditAnimalActivity::class.java
                )

                intent.putExtra("USER_ID", userId)
                intent.putExtra("ANIMAL_ID", animal.id)

                startActivity(intent)
            },
            { animal ->
                confirmDelete(animal)
            }
        )

        rvAnimals.adapter = adapter

        btnAddAnimal.setOnClickListener {

            val intent = Intent(
                this,
                AddEditAnimalActivity::class.java
            )

            intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadAnimals()
    }

    private fun loadAnimals() {

        animals.clear()

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT id, name, type, description, image, status, user_id
            FROM animals
            WHERE user_id = ?
            ORDER BY id DESC
            """.trimIndent(),
            arrayOf(userId.toString())
        )

        cursor.use {

            while (it.moveToNext()) {

                val animal = Animal(
                    id = it.getInt(0),
                    name = it.getString(1),
                    type = it.getString(2),
                    description = it.getString(3) ?: "",
                    image = it.getString(4),
                    status = it.getString(5),
                    userId = it.getInt(6)
                )

                animals.add(animal)
            }
        }

        db.close()

        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(animal: Animal) {

        AlertDialog.Builder(this)
            .setTitle("Supprimer l'animal")
            .setMessage(
                "Voulez-vous vraiment supprimer ${animal.name} ?"
            )
            .setPositiveButton("Supprimer") { _, _ ->

                deleteAnimal(animal)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun deleteAnimal(animal: Animal) {

        val db = dbHelper.writableDatabase

        val result = db.delete(
            "animals",
            "id = ? AND user_id = ?",
            arrayOf(
                animal.id.toString(),
                userId.toString()
            )
        )

        db.close()

        if (result > 0) {

            Toast.makeText(
                this,
                "Animal supprimé avec succès",
                Toast.LENGTH_SHORT
            ).show()

            loadAnimals()

        } else {

            Toast.makeText(
                this,
                "Erreur lors de la suppression",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
