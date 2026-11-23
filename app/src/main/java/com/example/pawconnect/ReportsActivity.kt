
package com.example.pawconnect

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReportsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var rvReports: RecyclerView
    private lateinit var adapter: ReportAdapter

    private val reports = ArrayList<Report>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        dbHelper = DBHelper(this)

        rvReports = findViewById(R.id.rvReports)

        rvReports.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(reports)

        rvReports.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadReports()
    }

    private fun loadReports() {

        reports.clear()

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT id, description, location, image, user_id
            FROM reports
            ORDER BY id DESC
            """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                val report = Report(
                    id = it.getInt(0),
                    description = it.getString(1),
                    location = it.getString(2),
                    image = it.getString(3),
                    userId = it.getInt(4)
                )

                reports.add(report)
            }
        }

        db.close()

        adapter.notifyDataSetChanged()

        if (reports.isEmpty()) {

            Toast.makeText(
                this,
                "Aucun signalement pour le moment",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
