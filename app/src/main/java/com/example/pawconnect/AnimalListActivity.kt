
package com.example.pawconnect

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AnimalListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var etSearchAnimal: EditText
    private lateinit var rvAnimalList: RecyclerView
    private lateinit var adapter: AnimalAdapter

    private val animals = ArrayList<Animal>()

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_list)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)

        etSearchAnimal = findViewById(R.id.etSearchAnimal)
        rvAnimalList = findViewById(R.id.rvAnimalList)

        rvAnimalList.layoutManager = LinearLayoutManager(this)

        adapter = AnimalAdapter(ArrayList()) { animal ->

            val intent = Intent(this, AnimalDetailsActivity::class.java)

            intent.putExtra("ANIMAL_ID", animal.id)
            intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }

        rvAnimalList.adapter = adapter

        etSearchAnimal.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                searchAnimals(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
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
            WHERE status = 'AVAILABLE'
            ORDER BY id DESC
            """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                val animal = Animal(
                    id = it.getInt(0),
                    name = it.getString(1),
                    type = it.getString(2),
                    description = it.getString(3),
                    image = it.getString(4),
                    status = it.getString(5),
                    userId = it.getInt(6)
                )

                animals.add(animal)
            }
        }

        db.close()

        searchAnimals(etSearchAnimal.text.toString())
    }

    private fun searchAnimals(query: String) {

        val filteredAnimals = ArrayList<Animal>()

        for (animal in animals) {

            if (animal.name.contains(query, ignoreCase = true) ||
                animal.type.contains(query, ignoreCase = true)
            ) {
                filteredAnimals.add(animal)
            }
        }

        adapter.updateList(filteredAnimals)
    }
}
