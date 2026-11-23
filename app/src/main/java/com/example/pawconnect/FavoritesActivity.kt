
package com.example.pawconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var rvFavorites: RecyclerView
    private lateinit var tvEmptyFavorites: TextView
    private lateinit var adapter: AnimalAdapter

    private val animals = ArrayList<Animal>()

    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)

        rvFavorites = findViewById(R.id.rvFavorites)
        tvEmptyFavorites = findViewById(R.id.tvEmptyFavorites)

        rvFavorites.layoutManager = LinearLayoutManager(this)

        adapter = AnimalAdapter(ArrayList()) { animal ->

            val intent = Intent(this, AnimalDetailsActivity::class.java)

            intent.putExtra("ANIMAL_ID", animal.id)
            intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }

        rvFavorites.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {

        animals.clear()

        if (userId == -1) {
            tvEmptyFavorites.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
            return
        }

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT animals.id, animals.name, animals.type,
                   animals.description, animals.image,
                   animals.status, animals.user_id
            FROM animals
            INNER JOIN favorites
            ON animals.id = favorites.animal_id
            WHERE favorites.user_id = ?
            ORDER BY favorites.id DESC
            """.trimIndent(),
            arrayOf(userId.toString())
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

        adapter.updateList(animals)

        if (animals.isEmpty()) {
            tvEmptyFavorites.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
        } else {
            tvEmptyFavorites.visibility = View.GONE
            rvFavorites.visibility = View.VISIBLE
        }
    }
}
