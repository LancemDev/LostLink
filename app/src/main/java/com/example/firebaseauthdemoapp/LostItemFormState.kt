package com.example.firebaseauthdemoapp

data class LostItemFormState(
    val category: String = "",
    val itemName: String = "",
    val description: String = "",
    val lastSeenLocation: String = "",
    val dateLost: Long = System.currentTimeMillis(),
    val additionalDetails: String = ""
)
