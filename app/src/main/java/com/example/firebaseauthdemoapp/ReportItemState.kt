package com.example.firebaseauthdemoapp

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.util.Date
import java.text.SimpleDateFormat

data class ReportItemState @RequiresApi(Build.VERSION_CODES.O) constructor(
    val itemName: String = "",
    val category: ItemCategory = ItemCategory.OTHER,
    val description: String = "",
    val lastSeenLocation: LatLng? = null,
    val locationDescription: String = "",
    val dateLost: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
    val characteristics: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val imageUrl: String = "",
    val status: String = ""
)

