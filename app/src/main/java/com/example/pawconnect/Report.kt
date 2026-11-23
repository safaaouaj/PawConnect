
package com.example.pawconnect

data class Report(
    val id: Int = 0,
    val description: String,
    val location: String,
    val image: String? = null,
    val userId: Int
)
