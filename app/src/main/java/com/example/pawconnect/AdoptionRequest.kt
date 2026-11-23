
package com.example.pawconnect

data class AdoptionRequest(
    val id: Int = 0,
    val userId: Int,
    val animalId: Int,
    val status: String = "PENDING"
)
