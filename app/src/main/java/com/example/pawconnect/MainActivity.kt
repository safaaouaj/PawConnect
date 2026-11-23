
package com.example.pawconnect

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userId = intent.getIntExtra("USER_ID", -1)

        val cardDiscoverAnimals =
            findViewById<LinearLayout>(R.id.cardDiscoverAnimals)

        val cardMyRequests =
            findViewById<LinearLayout>(R.id.cardMyRequests)

        val cardReportAnimal =
            findViewById<LinearLayout>(R.id.cardReportAnimal)

        val tvSeeAll =
            findViewById<TextView>(R.id.tvSeeAll)

        val navAnimals =
            findViewById<TextView>(R.id.navAnimals)

        val navFavorites =
            findViewById<TextView>(R.id.navFavorites)

        val navProfile =
            findViewById<TextView>(R.id.navProfile)

        cardDiscoverAnimals.setOnClickListener {
            openActivity(AnimalListActivity::class.java)
        }

        cardMyRequests.setOnClickListener {
            openActivity(MyRequestsActivity::class.java)
        }

        cardReportAnimal.setOnClickListener {
            openActivity(ReportActivity::class.java)
        }

        tvSeeAll.setOnClickListener {
            openActivity(AnimalListActivity::class.java)
        }

        navAnimals.setOnClickListener {
            openActivity(AnimalListActivity::class.java)
        }

        navFavorites.setOnClickListener {
            openActivity(FavoritesActivity::class.java)
        }

        navProfile.setOnClickListener {
            openActivity(ProfileActivity::class.java)
        }
    }

    private fun openActivity(activityClass: Class<*>) {

        val intent = Intent(this, activityClass)

        intent.putExtra("USER_ID", userId)

        startActivity(intent)
    }
}
