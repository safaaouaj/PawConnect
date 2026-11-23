
package com.example.pawconnect

data class Animal(
    val id: Int = 0,
    val name: String,
    val type: String,
    val description: String,
    val image: String? = null,
    val status: String = "AVAILABLE",
    val userId: Int
)
