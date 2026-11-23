
package com.example.pawconnect

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyRequestsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var rvMyRequests: RecyclerView
    private lateinit var tvEmptyRequests: TextView
    private lateinit var adapter: MyRequestAdapter

    private val requests = ArrayList<MyRequestItem>()

    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requests)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)

        rvMyRequests = findViewById(R.id.rvMyRequests)
        tvEmptyRequests = findViewById(R.id.tvEmptyRequests)

        rvMyRequests.layoutManager = LinearLayoutManager(this)

        adapter = MyRequestAdapter(requests)
        rvMyRequests.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadRequests()
    }

    private fun loadRequests() {

        requests.clear()

        if (userId == -1) {
            showEmptyMessage()
            return
        }

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT animals.name, adoption_requests.status
            FROM adoption_requests
            INNER JOIN animals
            ON adoption_requests.animal_id = animals.id
            WHERE adoption_requests.user_id = ?
            ORDER BY adoption_requests.id DESC
            """.trimIndent(),
            arrayOf(userId.toString())
        )

        cursor.use {

            while (it.moveToNext()) {

                val request = MyRequestItem(
                    animalName = it.getString(0),
                    status = it.getString(1)
                )

                requests.add(request)
            }
        }

        db.close()

        adapter.notifyDataSetChanged()

        if (requests.isEmpty()) {
            showEmptyMessage()
        } else {
            tvEmptyRequests.visibility = View.GONE
            rvMyRequests.visibility = View.VISIBLE
        }
    }

    private fun showEmptyMessage() {
        tvEmptyRequests.visibility = View.VISIBLE
        rvMyRequests.visibility = View.GONE
    }
}
