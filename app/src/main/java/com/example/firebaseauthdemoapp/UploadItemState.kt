package com.example.firebaseauthdemoapp

import com.example.firebaseauthdemoapp.ItemCategory
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Date

data class UploadItemState(
    val itemName: String = "",
    val category: ItemCategory = ItemCategory.OTHER,
    val description: String = "",
    val lastSeenLocation: LatLng? = null,
    val locationDescription: String = "",
    val dateLost: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
    val characteristics: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val status: String = ""
)
