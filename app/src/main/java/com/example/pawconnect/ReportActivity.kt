
package com.example.pawconnect

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class ReportActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    private lateinit var ivReportImage: ImageView
    private lateinit var btnChooseReportImage: Button
    private lateinit var etReportLocation: EditText
    private lateinit var etReportDescription: EditText
    private lateinit var btnSubmitReport: Button

    private var userId = -1
    private var imagePath: String? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

            if (uri != null) {
                saveImage(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("USER_ID", -1)

        ivReportImage = findViewById(R.id.ivReportImage)
        btnChooseReportImage = findViewById(R.id.btnChooseReportImage)
        etReportLocation = findViewById(R.id.etReportLocation)
        etReportDescription = findViewById(R.id.etReportDescription)
        btnSubmitReport = findViewById(R.id.btnSubmitReport)

        btnChooseReportImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnSubmitReport.setOnClickListener {
            submitReport()
        }
    }

    private fun saveImage(uri: Uri) {

        try {

            val inputStream = contentResolver.openInputStream(uri)

            if (inputStream == null) {
                Toast.makeText(
                    this,
                    "Impossible de lire cette image",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val imageFile = File(
                filesDir,
                "report_${System.currentTimeMillis()}.jpg"
            )

            inputStream.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }

            imagePath = imageFile.absolutePath

            ivReportImage.setImageURI(Uri.fromFile(imageFile))

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Erreur lors du chargement de la photo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun submitReport() {

        val location = etReportLocation.text.toString().trim()
        val description = etReportDescription.text.toString().trim()

        if (userId == -1) {
            Toast.makeText(
                this,
                "Utilisateur introuvable",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (location.isEmpty()) {
            etReportLocation.error = "Veuillez saisir le lieu"
            etReportLocation.requestFocus()
            return
        }

        if (description.isEmpty()) {
            etReportDescription.error = "Veuillez saisir une description"
            etReportDescription.requestFocus()
            return
        }

        val db = dbHelper.writableDatabase

        val values = android.content.ContentValues()
        values.put("description", description)
        values.put("location", location)
        values.put("image", imagePath)
        values.put("user_id", userId)

        val result = db.insert("reports", null, values)

        db.close()

        if (result != -1L) {

            Toast.makeText(
                this,
                "Votre signalement a été envoyé avec succès",
                Toast.LENGTH_LONG
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                "Erreur lors de l'envoi du signalement",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
